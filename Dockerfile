# Java 17を使うためのベースイメージ
FROM eclipse-temurin:17-jdk-jammy

# アプリのファイルをコピー
COPY . .

# Mavenの実行権限を付与してビルド
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# アプリを起動
CMD ["java", "-jar", "target/*.jar"]
