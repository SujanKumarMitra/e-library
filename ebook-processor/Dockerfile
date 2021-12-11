FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/ebook-processor-1.0.0.jar ebook-processor.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod" , "-jar", "/app/ebook-processor.jar"]
