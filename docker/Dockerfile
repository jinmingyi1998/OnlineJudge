FROM openjdk:8-jdk-alpine

MAINTAINER Mingyi Jin

COPY src /onlinejudge/src
COPY gradle /onlinejudge/gradle
COPY gradlew /onlinejudge
COPY settings.gradle /onlinejudge
COPY build.gradle /onlinejudge
WORKDIR /onlinejudge
RUN ./gradlew classes
CMD ./gradlew bootRun
