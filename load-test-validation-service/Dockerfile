#
# Build stage
#
FROM maven:3.6.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml package clean install

#
# Package stage
#
FROM openjdk:17-alpine
MAINTAINER pintertamas
COPY target/load-test-validation-service-docker.jar load-test-validation-service-docker.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/load-test-validation-service-docker.jar"]
