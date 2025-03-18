import psycopg2
import numpy as np
from sentence_transformers import SentenceTransformer

DB_NAME = "vectordb"
DB_USER = "postgres"
DB_PASSWORD = "1234"
DB_HOST = "localhost"
DB_PORT = "5432"

SIMILARITY_THRESHOLD = 0.5  # Reduzido para garantir que sempre haja resultado

model = SentenceTransformer("multi-qa-MiniLM-L6-dot-v1")

def connect_db():
    return psycopg2.connect(
        dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD, host=DB_HOST, port=DB_PORT
    )

def create_table():
    conn = connect_db()
    cur = conn.cursor()

    cur.execute("CREATE EXTENSION IF NOT EXISTS vector;")
    
    cur.execute(
        """
        CREATE TABLE IF NOT EXISTS text_embeddings (
            id SERIAL PRIMARY KEY,
            text TEXT NOT NULL,
            embedding vector(384)
        );
        """
    )
    
    # Criar índice HNSW para melhor eficiência
    cur.execute(
        """
        CREATE INDEX IF NOT EXISTS text_embeddings_embedding_idx
        ON text_embeddings USING hnsw (embedding vector_cosine_ops);
        """
    )
    
    conn.commit()
    cur.close()
    conn.close()

def get_embedding(text):
    return model.encode("Pergunta: " + text, convert_to_tensor=False).tolist()

def insert_text(text):
    paragraphs = text.split("\n\n")  # Segmentar por parágrafos
    conn = connect_db()
    cur = conn.cursor()

    for paragraph in paragraphs:
        paragraph = paragraph.strip()
        if len(paragraph) < 50:  # Evitar textos muito curtos
            continue

        embedding = get_embedding(paragraph)
        cur.execute(
            "INSERT INTO text_embeddings (text, embedding) VALUES (%s, %s)",
            (paragraph, embedding)
        )
    
    conn.commit()
    cur.close()
    conn.close()

def search_similar(query_text, top_k=5, context_window=1):
    query_embedding = get_embedding(query_text)
    query_embedding_str = f"[{', '.join(map(str, query_embedding))}]"

    conn = connect_db()
    cur = conn.cursor()
    cur.execute(
        """
        SELECT id, text, 1 - (embedding <=> %s::vector) AS similarity
        FROM text_embeddings
        ORDER BY embedding <=> %s::vector
        LIMIT %s;
        """,
        (query_embedding_str, query_embedding_str, top_k)
    )
    
    results = cur.fetchall()
    cur.close()
    conn.close()

    if not results:
        return [("Nenhum resultado exato encontrado, mas aqui estão os contextos mais próximos:", 0.0)]
    
    filtered_results = [(id, text, score) for id, text, score in results if score >= SIMILARITY_THRESHOLD]
    
    if not filtered_results:
        filtered_results = results  # Se nada passar do threshold, usa qualquer resultado
    
    merged_results = []
    used_ids = set()

    for i, (text_id, text, score) in enumerate(filtered_results):
        if text_id in used_ids:
            continue
        
        context_texts = [text]
        used_ids.add(text_id)

        for j in range(1, context_window + 1):
            next_idx = i + j
            if next_idx < len(filtered_results):
                next_id, next_text, _ = filtered_results[next_idx]
                if next_id not in used_ids:
                    context_texts.append(next_text)
                    used_ids.add(next_id)
        
        merged_results.append((" ".join(context_texts), score))

    return merged_results

if __name__ == "__main__":
    create_table()
    
    query = "Caixeiro viajante"
    results = search_similar(query)
    
    print("\n🔍 Resultados mais relevantes:")
    if not results:
        print("Nenhum resultado relevante encontrado.")
    else:
        for text, score in results:
            print(f"{score:.4f} - {text}")