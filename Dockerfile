FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# 依存関係を先にダウンロードして、ビルドを安定させます
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
