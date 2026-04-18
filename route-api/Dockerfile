FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
COPY route-api/build/libs/route-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java","-javaagent:/otel/opentelemetry-javaagent.jar","-jar","app.jar"]
