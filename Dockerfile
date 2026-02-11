# Estágio 1: Build da aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Estágio 2: Criação da imagem final com a JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR do estágio de build
COPY --from=build /app/target/*.jar /app/api.jar

EXPOSE 8080

CMD ["java", "-jar", "api.jar"]
