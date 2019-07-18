FROM openjdk:8-jdk-alpine
USER root
ADD target/*.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
