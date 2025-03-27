# Usar imagem base com Python
FROM python:3.9

# Definir diretório de trabalho dentro do container
WORKDIR /app

# Copiar os arquivos necessários para dentro do container
COPY requirements.txt .
COPY vector.py .

# Instalar dependências
RUN pip install --no-cache-dir -r requirements.txt

# Expor porta do Flask
EXPOSE 5001

# Comando para rodar a aplicação
CMD ["python", "vector.py"]