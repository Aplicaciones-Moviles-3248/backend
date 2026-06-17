# Multi-stage Dockerfile: build with Maven wrapper and run with Eclipse Temurin JRE 21
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the files needed for a build to leverage Docker cache
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN chmod +x mvnw && ./mvnw -B -DskipTests clean package

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar (assumes single jar in target produced by Maven)
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# Render provides environment variables at runtime; pass only what can't be read from properties
ENTRYPOINT ["sh", "-c", "JDBC_URL=$(echo $DB_URL | sed 's|postgresql://\\([^:]*\\):\\([^@]*\\)@\\(.*\\)|jdbc:postgresql://\\3?user=\\1\\&password=\\2|') && java -Dserver.port=${PORT:-8080} -Dspring.datasource.url=$JDBC_URL -jar /app/app.jar"]