FROM openjdk:15-jdk-alpine

WORKDIR /app

COPY . .

RUN ./gradlew bootJar --no-daemon

EXPOSE 8080

ENTRYPOINT ["java","-Duser.language=en",\
"-Dhibernate.types.print.banner=false",\
"-Duser.timezone=UTC",\
"-jar","build/libs/matchmaker.jar"]

