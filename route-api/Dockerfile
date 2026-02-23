FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/route-api/build/libs/route-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","app.jar"]