FROM openjdk:8-jdk-alpine

MAINTAINER Mingyi Jin

WORKDIR /app
COPY src src/
COPY gradle gradle/
COPY gradlew ./
COPY settings.gradle ./
COPY build.gradle ./
RUN chmod 755 gradlew && ./gradlew bootJar && cp build/libs/acm-0.0.1-SNAPSHOT.jar / && rm -rf * &&  mv /acm-0.0.1-SNAPSHOT.jar .
CMD java -jar acm-0.0.1-SNAPSHOT.jar
