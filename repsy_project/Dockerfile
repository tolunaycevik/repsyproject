FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY repsy-server/ ./repsy-server/
RUN mvn -f repsy-server/pom.xml -B clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /workspace/repsy-server/target/repsy-server-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
