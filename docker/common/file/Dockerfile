# 使用官方的 OpenJDK 8 镜像作为基础镜像
FROM openjdk:8-jdk

# 安装 unar 和其他必要的工具
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    unar \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /

# 将你的应用程序（例如 JAR 文件）复制到镜像中
COPY server.jar server.jar
# 暴露你的服务端口（如果需要）
EXPOSE 8080

# 启动你的 Java 服务
CMD ["java", "-jar", "server.jar"]
