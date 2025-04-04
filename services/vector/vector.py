import re
import logging
import psycopg2
import numpy as np
from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer

# Configurar logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

app = Flask(__name__)

# Configuração do banco
DB_NAME = "vectordb"
DB_USER = "postgres"
DB_PASSWORD = "1234"
DB_HOST = "host.docker.internal"
DB_PORT = "5432"

# Inicializar modelo de embeddings
logging.info(" >>> Carregando modelo de embeddings...")
model = SentenceTransformer("paraphrase-multilingual-mpnet-base-v2")
logging.info(" >>> Modelo carregado com sucesso.")

# Conectar ao banco
def get_db_connection():
    logging.info(" >>> Conectando ao banco de dados PostgreSQL...")
    return psycopg2.connect(
        dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD, host=DB_HOST, port=DB_PORT
    )

# Sanitização do nome da tabela
def sanitize_table_name(name):
    sanitized_name = re.sub(r"[^a-zA-Z0-9_]", "_", name)
    logging.info(f" >>> Nome da tabela sanitizado: {sanitized_name.lower()}")
    return sanitized_name.lower()

# Criar tabela de embeddings
def create_table(table_name):
    conn = get_db_connection()
    cur = conn.cursor()
    logging.info(f" >>> Criando tabela e índice para: {table_name}")

    cur.execute("CREATE EXTENSION IF NOT EXISTS vector;")
    cur.execute(f"""
        CREATE TABLE IF NOT EXISTS {table_name} (
            id SERIAL PRIMARY KEY,
            content TEXT,
            embedding VECTOR(768)
        );
    """)
    cur.execute(f"""
        CREATE INDEX IF NOT EXISTS idx_{table_name} ON {table_name} 
        USING hnsw (embedding vector_l2_ops) 
        WITH (m = 32, ef_construction = 300);
    """)
    conn.commit()
    cur.close()
    conn.close()
    logging.info(" >>> Tabela criada com sucesso.")

# Sanitização do nome da tabela
def clean_text(text):
    text = text.replace("\n", " ")
    text = re.sub(r"\s+", " ", text).strip()
    return text

# Dividir texto em chunks
def split_text(text, chunk_size=500, overlap=50):
    logging.info(" >>> Dividindo texto em chunks...")
    text = clean_text(text)
    
    sentences = text.split('. ')
    chunks = []
    current = ""
    for sentence in sentences:
        if len(current) + len(sentence) < chunk_size:
            current += sentence + ". "
        else:
            chunks.append(current.strip())
            current = sentence + ". "
    if current:
        chunks.append(current.strip())
    logging.info(" >>> Texto dividido com sucesso.")
    return chunks

# Inserir embeddings
def insert_embeddings(table_name, text):
    logging.info(f" >>> Iniciando inserção de embeddings na tabela: {table_name}")
    create_table(table_name)
    chunks = split_text(text)
    embeddings = model.encode(chunks)

    conn = get_db_connection()
    cur = conn.cursor()

    for chunk, emb in zip(chunks, embeddings):
        emb_list = emb.tolist()
        cur.execute(
            f"INSERT INTO {table_name} (content, embedding) VALUES (%s, %s)",
            (chunk, emb_list)
        )

    conn.commit()
    cur.close()
    conn.close()
    logging.info(" >>> Inserção concluída com sucesso.")
    return len(chunks)

# Endpoint para inserir texto e criar uma nova tabela
@app.route("/insert", methods=["POST"])
def insert():
    logging.info(" >>> Requisição recebida em /insert")
    
    data = request.json
    text = data.get("text", "")
    name = data.get("name", "")

    if not text or not name:
        logging.warning(" >>> Campos 'text' e 'name' são obrigatórios.")
        return jsonify({"error": "Campos 'text' e 'name' são obrigatórios"}), 400

    table_name = sanitize_table_name(name)
    insert_embeddings(table_name, text)

    logging.info(f" >>> Inserção finalizada. Tabela '{table_name}' criada.")

    return jsonify({"vectorReference": table_name})

# Endpoint para buscar trechos relevantes (resposta em texto único)
@app.route("/query", methods=["POST"])
def query():
    logging.info(" >>> Requisição recebida em /query")
    
    data = request.json
    question = data.get("question", "")
    table_name = data.get("vectorReference", "")

    if not question or not table_name:
        logging.warning(" >>> Campos 'question' e 'vectorReference' são obrigatórios.")
        return jsonify({"error": "Campos 'question' e 'vectorReference' são obrigatórios"}), 400

    table_name = sanitize_table_name(table_name)
    logging.info(f" >>> Executando busca na tabela: {table_name}")
    logging.info(f" >>> Pergunta recebida: {question}")

    question_emb = model.encode([question])[0].tolist()
    question_emb_str = f"ARRAY{question_emb}::vector"

    conn = get_db_connection()
    cur = conn.cursor()

    cur.execute(f"""
        SELECT content
        FROM {table_name}
        ORDER BY embedding <-> {question_emb_str}
        LIMIT 10;
    """)
    
    results = cur.fetchall()
    cur.close()
    conn.close()

    combined_text = "\n".join([row[0] for row in results])
    logging.info(" >>> Consulta finalizada com sucesso.")

    return jsonify({"content": combined_text})

# Endpoint para deletar a tabela especificada em "vectorReference"
@app.route("/delete", methods=["POST"])
def delete():
    logging.info(" >>> Requisição recebida em /delete")
    
    data = request.json
    table_name = data.get("vectorReference", "")

    if not table_name:
        logging.warning(" >>> Campo 'vectorReference' é obrigatório.")
        return jsonify({"error": "Campo 'vectorReference' é obrigatório"}), 200

    table_name = sanitize_table_name(table_name)

    try:
        conn = get_db_connection()
        cur = conn.cursor()
        logging.info(f" >>> Deletando tabela: {table_name}")
        cur.execute(f"DROP TABLE IF EXISTS {table_name};")
        conn.commit()
        cur.close()
        conn.close()
        logging.info(f" >>> Tabela '{table_name}' deletada com sucesso.")
        return jsonify({"message": f"Tabela '{table_name}' deletada com sucesso."}), 200
    except Exception as e:
        logging.error(f" >>> Erro ao deletar tabela: {e}")
        return jsonify({"error": "Erro ao deletar a tabela."}), 500

# Rodar API
if __name__ == "__main__":
    logging.info(" >>> Inicializando servidor Flask na porta 5001...")
    app.run(host="0.0.0.0", port=5001, debug=True)