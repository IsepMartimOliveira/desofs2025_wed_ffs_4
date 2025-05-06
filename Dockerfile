# Dockerfile

FROM eclipse-temurin:17-jdk
ARG JAR_FILE=demoOrm-0.0.1-SNAPSHOT.jar
COPY target/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

