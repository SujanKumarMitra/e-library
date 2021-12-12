FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/notification-service-1.0.0.jar notification-service.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod" , "-jar", "/app/notification-service.jar"]
