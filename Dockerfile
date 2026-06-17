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
ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -Dspring.datasource.url=${DATABASE_URL} -Dauthorization.jwt.secret=${AUTHORIZATION_JWT_SECRET} -Dauthorization.jwt.expiration.days=${AUTHORIZATION_JWT_EXPIRATION_DAYS} -jar /app/app.jar"]