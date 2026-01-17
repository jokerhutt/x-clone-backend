FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve -Djava.net.preferIPv4Stack=true

COPY src ./src
RUN mvn clean package -DskipTests -Djava.net.preferIPv4Stack=true

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/xclone-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]