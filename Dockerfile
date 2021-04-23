FROM maven:3.6.3-openjdk-11-slim AS build

WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:11.0.10-jre-slim

COPY --from=build /build/simplq/target/simplq-0.0.1-SNAPSHOT.jar app.jar

ENV profile dev

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${profile}", "/app.jar"]
