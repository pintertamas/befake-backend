#
# Build stage
#
FROM maven:3.6.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17-alpine
MAINTAINER pintertamas
COPY target/api-gateway-latest.jar api-gateway-latest.jar
ENTRYPOINT ["java","-jar","/api-gateway-latest.jar"]