FROM alpine:3.14 as base
MAINTAINER Yusuf Murodov "yusuf.murodov1@gmail.com"
WORKDIR /app
RUN apk update \
  && apk upgrade \
  && apk add --update openjdk11 tzdata curl unzip bash \
  && rm -rf /var/cache/apk/*
COPY . /app
RUN chmod +x ./gradlew \
  && ./gradlew build -x test

FROM base as development
CMD ["./gradlew", "bootRun", "-Dspring.profiles.active=dev", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8001'"]

FROM base as build
RUN ./gradlew jar

FROM openjdk:11-jre-slim as production
COPY --from=build /app/build/libs/song-service-*.jar /song-service-reactive-r2dbc.jar
CMD ["java", "-Dspring.profiles.active=prod", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8001", "-jar", "/song-service-reactive-r2dbc.jar"]
