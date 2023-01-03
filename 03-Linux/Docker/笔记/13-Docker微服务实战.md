# 13-Docker微服务实战

## 一、通过IDEA新建一个普通微服务模块
详见 docker_boot


## 二、通过dockerfile发布微服务部署到docker容器
编写Dockerfile  
Dockerfile内容  
```
# 基础镜像使用java
FROM java:8
# 作者
MAINTAINER zhuangpf
# VOLUME 指定临时文件目录为/tmp，在主机/var/lib/docker目录下创建了一个临时文件并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为zzyy_docker.jar
ADD docker_boot-1.0-SNAPSHOT.jar zpf_docker.jar
# 运行jar包
RUN bash -c 'touch /zpf_docker.jar'
ENTRYPOINT ["java","-jar","/zpf_docker.jar"]
#暴露6001端口作为微服务
EXPOSE 6001
```
将微服务jar包和Dockerfile文件上传到同一个目录下/mydocker


构建镜像
```
docker build -t zpf_docker:1.6 .
```

运行容器
```
docker run -d -p 6001:6001 zpf_docker:1.6
```

