import logging
import os
from io import BytesIO
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
def carregar_encodes_cadastrados(conn):
    """
    Lê todos os registros da tabela 'patient', extrai o encode do rosto da imagem armazenada.
    Retorna três listas paralelas: [encodes], [ids], [nomes].
    """
    logging.info("Carregando encodes cadastrados do banco de dados...")
    with conn.cursor() as cursor:
        cursor.execute("SELECT id, name, patient_picture FROM public.patient WHERE patient_picture IS NOT NULL;")
        rows = cursor.fetchall()

    encodings = []
    ids = []
    names = []

    for row in rows:
        user_id, nome, foto_bytes = row

        if not foto_bytes:
            logging.info(f"[AVISO] Sem imagem para o paciente '{nome}'.")
            continue

        try:
            image = face_recognition.load_image_file(BytesIO(foto_bytes))
        except Exception as e:
            logging.info(f"[ERRO] Erro ao carregar imagem de {nome}: {e}")
            continue

        face_locations = face_recognition.face_locations(image)
        if len(face_locations) == 0:
            logging.info(f"[AVISO] Não foi possível detectar rosto na imagem de '{nome}'.")
            continue

        face_encodes = face_recognition.face_encodings(image, face_locations)
        if face_encodes:
            encodings.append(face_encodes[0])
            ids.append(user_id)
            names.append(nome)

    logging.info(f"Total de encodes carregados: {len(encodings)}")
    return encodings, ids, names

# --------------------------------------------------------------
@app.route("/recognize", methods=["POST"])
def recognize_face():
    logging.info("Endpoint /recognize chamado.")
    file = request.files.get("file")

    iniciar_ambiente()

    if not file:
        logging.info("Nenhum arquivo foi enviado na requisição.")
        return jsonify({"message": "Arquivo não encontrado na requisição.", "severity": "error"}), 400

    try:
        image_bytes = file.read()
        logging.info("Imagem lida com sucesso a partir do arquivo enviado.")
    except Exception as e:
        logging.info(f"Erro ao ler o arquivo: {str(e)}")
        return jsonify({"message": "Erro ao ler o arquivo.", "severity": "error"}), 400

    np_arr = np.frombuffer(image_bytes, np.uint8)
    frame = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    if frame is None:
        logging.info("Não foi possível decodificar a imagem. Verifique o formato enviado.")
        return jsonify({"message": "Não foi possível decodificar a imagem.", "severity": "error"}), 400

    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    logging.info("Detectando rostos na imagem...")
    face_locations = face_recognition.face_locations(rgb_frame)
    face_encodings = face_recognition.face_encodings(rgb_frame, face_locations)

    if not face_encodings:
        logging.info("Nenhum rosto foi detectado na imagem.")
        return jsonify({"message": "Nenhum rosto detectado.", "severity": "error"}), 400

    resultados = []
    conn = get_db_connection()

    for face_encoding in face_encodings:
        logging.info("Comparando o rosto detectado com a base de encodes conhecidos...")
        matches = face_recognition.compare_faces(known_face_encodings, face_encoding, tolerance=0.5)
        face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)

        if face_distances.size > 0:
            best_match_index = np.argmin(face_distances)
            if matches[best_match_index]:
                user_id = known_face_ids[best_match_index]
                nome = known_face_names[best_match_index]
                msg = f"{nome} reconhecido."
                logging.info(msg)
                resultados.append({"userId": user_id, "name": nome})
            else:
                logging.info("Rosto não corresponde a ninguém na base.")
        else:
            logging.info("Não há encodes conhecidos para comparar.")

    conn.close()
    logging.info("Reconhecimento facial finalizado. Retornando resultados.")

    if not resultados:
        return jsonify({"message": "Rosto não corresponde a ninguém na base.", "severity": "info"}), 404

    return jsonify({"userId": user_id}), 200

# --------------------------------------------------------------
def iniciar_ambiente():
    global known_face_encodings, known_face_ids, known_face_names
    logging.info("[STARTUP] Iniciando aplicação Flask e preparando ambiente...")

    conn = get_db_connection()
    known_face_encodings, known_face_ids, known_face_names = carregar_encodes_cadastrados(conn)
    conn.close()

    logging.info(f"[STARTUP] Ambiente preparado com sucesso. Encodes carregados: {len(known_face_encodings)}")

# --------------------------------------------------------------
# Se quiser rodar diretamente este arquivo:
if __name__ == "__main__":
    logging.info("Iniciando servidor Flask...")
    app.run(host="0.0.0.0", port=5002, debug=True)