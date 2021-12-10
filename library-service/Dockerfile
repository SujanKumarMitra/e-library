FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/library-service-1.0.0.jar library-service.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod" , "-jar", "/app/library-service.jar"]
