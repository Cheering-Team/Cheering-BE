FROM amazoncorretto:17
ARG JAR_FILE=./build/libs/*.jar
ARG PROFILES
ARG ENV

RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone

ENV TZ=Asia/Seoul

COPY ${JAR_FILE} app.jar
COPY src/main/resources/firebase.json /app/src/main/resources/firebase.json
COPY src/main/resources/db/migration /app/src/main/resources/db/migration
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
