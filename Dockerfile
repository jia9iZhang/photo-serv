
# 使用 Azul Zulu JDK 17 镜像作为基础镜像
FROM azul/zulu-openjdk-alpine:17

# 设置工作目录
WORKDIR /app

# 将打包好的 JAR 文件复制到镜像中
COPY target/photo-serv-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（根据你的应用程序调整）
EXPOSE 8080

# 启动应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]
