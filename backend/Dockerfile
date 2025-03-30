# Etapa 1: Build com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia o pom.xml e resolve dependências antecipadamente (cache eficiente)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copia o restante do código e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem leve apenas com o JAR
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copia o JAR do build anterior
COPY --from=builder /app/target/hitic-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]