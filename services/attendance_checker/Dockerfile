FROM python:3.10-slim

# ----------------------------------------------------------
# Instala dependências do sistema
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    libsm6 \
    libxext6 \
    libxrender-dev \
    libglib2.0-0 \
    libgl1-mesa-glx \
    libopencv-dev \
    python3-opencv \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# ----------------------------------------------------------
# Define diretório de trabalho
WORKDIR /app

# Copia arquivos do projeto
COPY checker.py ./
COPY requirements.txt ./

# ----------------------------------------------------------
# Instala dependências do Python
RUN pip install --no-cache-dir -r requirements.txt

# ----------------------------------------------------------
# Expõe porta usada pelo Flask
EXPOSE 5002

# ----------------------------------------------------------
# Comando para iniciar o serviço
CMD ["python", "checker.py"]