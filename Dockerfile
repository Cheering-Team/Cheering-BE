FROM amazoncorretto:17
ARG JAR_FILE=./build/libs/*.jar
ARG PROFILES
ARG ENV

RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone

ENV TZ=Asia/Seoul

COPY ${JAR_FILE} app.jar
COPY src/main/resources/application.yml /app/config/application.yml
# ENTRYPOINT ["java", "-Dspring.config.location=/app/config/application.yml", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
# 실행 환경을 bash로 임시 설정
CMD ["bash"]
