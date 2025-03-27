FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src/main/resources/application*.yml src/main/resources/

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /application
COPY --from=build /app/target/eureka-service-0.0.1-SNAPSHOT.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./

EXPOSE 8761

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-cp", ".", "org.springframework.boot.loader.launch.JarLauncher"]