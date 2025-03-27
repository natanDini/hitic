# Usar uma imagem Node.js como base para a construção
FROM node:20 AS build

# Definir diretório de trabalho dentro do container
WORKDIR /app

# Copiar os arquivos do projeto Angular
COPY package.json package-lock.json ./
RUN npm install

COPY . .

# Construir o projeto Angular para produção
RUN npm run build --prod

# Usar uma imagem Nginx para servir a aplicação Angular
FROM nginx:latest

# Copiar os arquivos construídos para o Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Expor a porta padrão do Nginx
EXPOSE 80

# Comando para iniciar o servidor Nginx
CMD ["nginx", "-g", "daemon off;"]