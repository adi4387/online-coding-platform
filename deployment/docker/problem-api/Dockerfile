# Build stage

FROM maven:3.8.4-openjdk-17-slim AS BUILD_STAGE
WORKDIR /app
COPY . .
RUN --mount=type=cache,id=m2-cache,sharing=shared,target=/root/.m2 mvn clean install -DskipTests=true

# Run stage

FROM eclipse-temurin:17.0.6_10-jre-alpine
WORKDIR /app

USER root

COPY --from=BUILD_STAGE /app/problem-api/target/*.jar problem-api.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "problem-api.jar"]
