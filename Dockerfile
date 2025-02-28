FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/JavaCodeTest-1.0.jar /app/JavaCodeTest-1.0.jar

ENTRYPOINT ["java", "-jar", "/app/JavaCodeTest-1.0.jar"]
