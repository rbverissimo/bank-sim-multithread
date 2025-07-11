FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY --from=build /app/target/bank-sim-multithread-1.0-SNAPSHOT-jar-with-dependencies.jar .

CMD ["java", "-jar", "bank-sim-multithread-1.0-SNAPSHOT-jar-with-dependencies.jar"]