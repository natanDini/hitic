# Usar uma imagem base com Python e utilitários necessários
FROM ubuntu:20.04

# Definir variáveis de ambiente para evitar prompts interativos
ENV DEBIAN_FRONTEND=noninteractive

# Atualizar pacotes e instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    poppler-utils \
    python3 \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho no container
WORKDIR /app

# Copiar os arquivos do serviço
COPY requirements.txt .
COPY ocr.py .

# Instalar dependências do Python
RUN pip install --no-cache-dir -r requirements.txt

# Expor porta do Flask
EXPOSE 5002

# Comando para rodar a aplicação
CMD ["python3", "ocr.py"]