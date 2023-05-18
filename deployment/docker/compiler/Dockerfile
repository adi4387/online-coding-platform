# Build stage

FROM maven:3.8.4-openjdk-17-slim AS BUILD_STAGE
WORKDIR /app
COPY . .
RUN --mount=type=cache,id=m2-cache,sharing=shared,target=/root/.m2 mvn clean install -DskipTests=true

# Run stage

FROM openjdk:17.0.1-jdk-slim
WORKDIR /compiler

USER root

RUN apt update && apt install -y docker.io

COPY --from=BUILD_STAGE /app/compiler/target/*.jar compiler.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "compiler.jar"]
