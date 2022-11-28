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
COPY target/api-gateway-latest.jar api-gateway-latest.jar
EXPOSE 8765
#CMD [ "java $JAVA_OPTS -jar ./api-gateway-latest.jar $MAIN_OPTS" ]
#CMD [ "/bin/sh", "-c", "java $JAVA_OPTS -jar /api-gateway-latest.jar"]
ENTRYPOINT ["java","-jar","/api-gateway-latest.jar"]