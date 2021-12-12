FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/authorization-service-1.0.0.jar authorization-service.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod" , "-jar", "/app/authorization-service.jar"]
