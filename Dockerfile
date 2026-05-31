# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S raizes && adduser -S raizes -G raizes
RUN mkdir -p /app/data && chown raizes:raizes /app/data

COPY --from=build /app/target/*.jar app.jar

USER raizes

EXPOSE 8080

ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/raizes.db
ENV JWT_SECRET=raizes-nordeste-secret-key-2026-muito-segura
ENV JWT_EXPIRATION_MS=3600000

ENTRYPOINT ["java", "-jar", "app.jar"]
