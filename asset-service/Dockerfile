FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/asset-service-1.0.0.jar asset-service.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod" , "-jar", "/app/asset-service.jar"]
