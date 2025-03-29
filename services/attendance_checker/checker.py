import logging
import os
import psycopg2
import face_recognition
import numpy as np
import cv2
from flask import Flask, request, jsonify

# --------------------------------------------------------------
# Configure logging
logging.basicConfig(level=logging.INFO)

# --------------------------------------------------------------
# Carregar variáveis de ambiente ou defina manualmente
DB_NAME = os.environ.get("DB_NAME", "hitic")
DB_USER = os.environ.get("DB_USER", "postgres")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "1234")
DB_HOST = os.environ.get("DB_HOST", "host.docker.internal")
DB_PORT = os.environ.get("DB_PORT", "5432")

# --------------------------------------------------------------
app = Flask(__name__)

# Estas listas ficarão na memória com base nos dados do DB
known_face_encodings = []
known_face_ids = []
known_face_names = []

# --------------------------------------------------------------
def get_db_connection():
    logging.info("Estabelecendo conexão com o banco de dados...")
    return psycopg2.connect(
        dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD,
        host=DB_HOST, port=DB_PORT
    )

# --------------------------------------------------------------
def criar_tabela_se_nao_existir(conn):
    logging.info("Criando a tabela 'agendado' se não existir...")
    with conn.cursor() as cursor:
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS agendado (
                id SERIAL PRIMARY KEY,
                nome TEXT NOT NULL,
                foto_path TEXT NOT NULL,
                status TEXT DEFAULT 'ausente'
            );
        """)
        conn.commit()

# --------------------------------------------------------------
def carregar_encodes_cadastrados(conn):
    """
    Lê todos os registros de 'agendado', carrega a foto e extrai o encode do rosto.
    Retorna três listas paralelas: [encodes], [ids], [nomes].
    """
    logging.info("Carregando encodes cadastrados do banco de dados...")
    with conn.cursor() as cursor:
        cursor.execute("SELECT id, nome, foto_path, status FROM public.agendado;")
        rows = cursor.fetchall()

    encodings = []
    ids = []
    names = []

    for row in rows:
        logging.info(f" >>>>> Lendo row={row}")
        user_id, nome, foto_path, status = row

        logging.info(f"Verificando caminho: {foto_path}")
        logging.info(f"Existe? {os.path.exists(foto_path)}")

        if not os.path.exists(foto_path):
            logging.info(f"[AVISO] A foto '{foto_path}' não foi encontrada para {nome}.")
            continue

        image = face_recognition.load_image_file(foto_path)
        face_locations = face_recognition.face_locations(image)

        if len(face_locations) == 0:
            logging.info(f"[AVISO] Não foi possível detectar rosto em '{foto_path}'.")
            continue

        face_encodes = face_recognition.face_encodings(image, face_locations)
        if len(face_encodes) > 0:
            encodings.append(face_encodes[0])
            ids.append(user_id)
            names.append(nome)

    logging.info(f"Total de encodes carregados: {len(encodings)}")
    return encodings, ids, names

# --------------------------------------------------------------
def marcar_presenca(conn, user_id):
    """
    Atualiza o status de uma pessoa para 'presente' no banco de dados.
    """
    logging.info(f"Marcando presença para o usuário com ID {user_id}...")
    with conn.cursor() as cursor:
        cursor.execute("UPDATE agendado SET status = 'presente' WHERE id = %s", (user_id,))
    conn.commit()

# --------------------------------------------------------------
def iniciar_ambiente():
    global known_face_encodings, known_face_ids, known_face_names
    logging.info("[STARTUP] Iniciando aplicação Flask e preparando ambiente...")

    conn = get_db_connection()
    criar_tabela_se_nao_existir(conn)
    known_face_encodings, known_face_ids, known_face_names = carregar_encodes_cadastrados(conn)
    conn.close()

    logging.info(f"[STARTUP] Ambiente preparado com sucesso. Encodes carregados: {len(known_face_encodings)}")

# --------------------------------------------------------------
@app.route("/", methods=["GET"])
def root():
    logging.info("Rota raiz acessada. Retornando mensagem de status.")
    return jsonify({"message": "API de Reconhecimento Facial ativa."})

# --------------------------------------------------------------
@app.route("/recognize", methods=["POST"])
def recognize_face():
    logging.info("Endpoint /recognize chamado.")
    file = request.files.get("file")

    if not file:
        logging.info("Nenhum arquivo foi enviado na requisição.")
        return jsonify({"message": "Arquivo não encontrado na requisição.", "severity": "error"}), 400

    try:
        image_bytes = file.read()
        logging.info("Imagem lida com sucesso a partir do arquivo enviado.")
    except Exception as e:
        logging.info(f"Erro ao ler o arquivo: {str(e)}")
        return jsonify({"message": "Erro ao ler o arquivo.", "severity": "error"}), 400

    # Converte bytes para array numpy
    np_arr = np.frombuffer(image_bytes, np.uint8)
    frame = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    if frame is None:
        logging.info("Não foi possível decodificar a imagem. Verifique o formato enviado.")
        return jsonify({"message": "Não foi possível decodificar a imagem.", "severity": "error"}), 400

    # Converte BGR -> RGB
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # Detecta rostos
    logging.info("Detectando rostos na imagem...")
    face_locations = face_recognition.face_locations(rgb_frame)
    face_encodings = face_recognition.face_encodings(rgb_frame, face_locations)

    if len(face_encodings) == 0:
        logging.info("Nenhum rosto foi detectado na imagem.")
        return jsonify({"message": "Nenhum rosto detectado.", "severity": "error"}), 200

    resultados = []
    conn = get_db_connection()

    for face_encoding in face_encodings:
        # Compara com encodes já cadastrados
        logging.info("Comparando o rosto detectado com a base de encodes conhecidos...")
        matches = face_recognition.compare_faces(known_face_encodings, face_encoding, tolerance=0.5)
        face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)

        if len(face_distances) > 0:
            best_match_index = np.argmin(face_distances)
            if matches[best_match_index]:
                user_id = known_face_ids[best_match_index]
                nome = known_face_names[best_match_index]
                marcar_presenca(conn, user_id)
                msg = f"{nome} reconhecido e marcado como presente."
                logging.info(msg)
                resultados.append(msg)
            else:
                logging.info("Rosto não corresponde a ninguém na base.")
                resultados.append("Rosto não reconhecido ou não corresponde a ninguém na base.")
        else:
            logging.info("Não há encodes conhecidos para comparar.")
            resultados.append("Não há encodes conhecidos para comparar.")

    conn.close()
    logging.info("Reconhecimento facial finalizado. Retornando resultados.")
    return jsonify({"message": "\n".join(resultados), "severity": "info"}), 200

# --------------------------------------------------------------
# Se quiser rodar diretamente este arquivo:
if __name__ == "__main__":
    iniciar_ambiente()
    logging.info("Iniciando servidor Flask...")
    app.run(host="0.0.0.0", port=5002, debug=True)