import pytesseract
from pdf2image import convert_from_bytes
from flask import Flask, request, jsonify
import logging

app = Flask(__name__)

# Configurar Tesseract e Poppler para uso no Docker
pytesseract.pytesseract.tesseract_cmd = "/usr/bin/tesseract"
POPPLER_PATH = "/usr/bin"

logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

@app.route('/ocr/upload', methods=['POST'])
def upload_pdf():
    logging.info(" >>> Nova requisição recebida em /ocr/upload")

    if 'file' not in request.files:
        logging.warning(" >>> Nenhum arquivo enviado na requisição.")
        return jsonify({"error": "Nenhum arquivo enviado"}), 400

    file = request.files['file']

    if file.filename == '':
        logging.warning(" >>> Nome de arquivo inválido.")
        return jsonify({"error": "Nome de arquivo inválido"}), 400

    try:
        logging.info(f" >>> Processando arquivo: {file.filename}")

        # Converte PDF para imagens na memória
        images = convert_from_bytes(file.read(), poppler_path=POPPLER_PATH)
        logging.info(f" >>> Extração de imagens concluída ({len(images)} páginas detectadas).")

        # Aplica OCR nas imagens
        text = "\n".join([pytesseract.image_to_string(img, lang="por") for img in images])
        logging.info(" >>> OCR concluído com sucesso.")

        return text

    except Exception as e:
        logging.error(f" >>> Erro ao processar o PDF: {e}", exc_info=True)
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    logging.info(" >>> Servidor OCR iniciado!")
    app.run(host="0.0.0.0", port=5000, debug=True)