# Docker image for springboot file run
# VERSION 0.0.1
# Author: ltz
# 基础镜像使用java
FROM java:8
# 作者
MAINTAINER liaotianzheng <liaotianzheng@qq.com>
VOLUME /work/logs/config/:/logs/
ADD spiderserver-1.0-SNAPSHOT.jar spiderserver.jar
EXPOSE 9996
RUN bash -c "touch /ltzusercenter.jar"
ENTRYPOINT ["java", "-jar", "spiderserver.jar","> /logs/spiderserver.log"]