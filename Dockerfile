# 1. 建置階段 (Builder Stage)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
# 賦予 mvnw 執行權限並打包 (跳過測試以加速)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# 2. 執行階段 (Runtime Stage)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 只複製打包好的 jar 檔，讓映像檔更輕量
COPY --from=builder /app/target/*.jar app.jar

# 設定記憶體限制，避免在 Render 免費版崩潰
ENV JAVA_OPTS="-Xmx350m -Xms350m"

# 啟動指令
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]