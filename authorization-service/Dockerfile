#
# Build stage
#
FROM maven:3.6.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package
RUN mvn -f /home/app/pom.xml install

#
# Package stage
#
FROM openjdk:17-alpine
MAINTAINER pintertamas
COPY target/authorization-service-latest.jar authorization-service-latest.jar
ENTRYPOINT ["java","-jar","pintertamas/authorization-service-latest.jar"]