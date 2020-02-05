FROM openjdk:8u242-jre-slim

RUN mkdir /conf
COPY target/spring-final-project-server-1.0.0-SNAPSHOT.jar app.jar
COPY ./target/classes/application.properties config/application.properties

ENTRYPOINT ["java", "-jar", "app.jar"]