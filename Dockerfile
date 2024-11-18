FROM amazoncorretto:17
ARG JAR_FILE=./build/libs/*.jar
ARG PROFILES
ARG ENV

RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone

ENV TZ=Asia/Seoul

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "--debug", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
