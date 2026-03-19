# 1. ビルド用環境（MavenとJavaが入ったイメージをRenderに用意させる）
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# 全ファイルをコピー（これでsrcも届きます）
COPY . .

# mvnwを使わず、システム側のmvnコマンドを直接実行する
RUN mvn clean package -DskipTests

# 2. 実行用環境
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# ビルドした成果物（jar）をコピー
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
