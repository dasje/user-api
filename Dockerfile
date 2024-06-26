FROM gradle:8-jdk20 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:20
EXPOSE 8080:8080
RUN mkdir /app
COPY .env /app
COPY swagger_docs.yaml /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/user-api.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","/app/user-api.jar"]