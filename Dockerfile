FROM gradle:5.2.1-jdk8-alpine
COPY .  /app
WORKDIR /app
RUN chmod 755 gradlew && ./gradlew bootJar && mv build/libs/acm-0.0.1-SNAPSHOT.jar . &&  rm -rf .git src out .gradle build && gradle --stop
CMD java -jar acm-0.0.1-SNAPSHOT.jar
