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
COPY target/api-gateway-kubernetes.jar api-gateway-kubernetes.jar
EXPOSE 8765
#CMD [ "java $JAVA_OPTS -jar ./api-gateway-kubernetes.jar $MAIN_OPTS" ]
#CMD [ "/bin/sh", "-c", "java $JAVA_OPTS -jar /api-gateway-kubernetes.jar"]
ENTRYPOINT ["java","-jar","/api-gateway-kubernetes.jar"]