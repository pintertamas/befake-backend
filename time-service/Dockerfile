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
COPY target/time-service-latest.jar time-service-latest.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/time-service-latest.jar"]
