FROM alpine:3.11
COPY .  /app
WORKDIR /app
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk add openjdk11-jdk \
    && chmod 755 gradlew && ./gradlew --no-daemon bootJar \
    && mv build/libs/acm-0.0.1-SNAPSHOT.jar . \
    &&  rm -rf .git src out .gradle build \
    && rm -rf /root/.gradle
EXPOSE 8080
VOLUME /onlinejudge
CMD java -jar acm-0.0.1-SNAPSHOT.jar
