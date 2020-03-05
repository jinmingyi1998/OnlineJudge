FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.6_10-slim
COPY .  /app
WORKDIR /app
RUN chmod 755 gradlew && ./gradlew --no-daemon bootJar \
    && mv build/libs/acm-0.0.1-SNAPSHOT.jar . \
    &&  rm -rf .git src out .gradle build \
    && rm -rf /root/.gradle
EXPOSE 8080
VOLUME /onlinejudge
CMD java -jar acm-0.0.1-SNAPSHOT.jar
