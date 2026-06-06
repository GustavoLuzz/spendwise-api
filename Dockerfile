FROM maven:3.9.10-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spendwise && adduser -S spendwise -G spendwise

WORKDIR /app
COPY --from=build --chown=spendwise:spendwise /app/target/*.jar app.jar

USER spendwise

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
