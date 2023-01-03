# 07-Docker容器数据卷

## 一、问题
```
--privileged=true
```
Docker挂载主机目录访问如果出现cannot open directory .: Permission denied  
解决办法：  
在挂载目录后多加一个--privileged=true参数即可。  
如果是CentOS7安全模块会比之前系统版本加强，不安全的会先禁止，所以目录挂载的情况被默认为不安全的行为。  
在SELinux里面挂载目录被禁止掉了额，如果要开启，我们一般使用--privileged=true命令，扩大容器的权限解决挂载目录没有权限的问题，也即使用该参数，container内的root拥有真正的root权限，否则，container内的root只是外部的一个普通用户权限。  


## 二、参数-v
```
docker run -d -p 5000:5000  -v /deploy/registry/:/tmp/registry --privileged=true registry
```
默认情况下，仓库被创建再容器的 /var/lib/registry 目录下，建议自行用容器卷映射，方便于主机联调。   

## 三、容器数据卷
卷就是目录或文件，存在于一个或多个容器中，由docker挂载到容器，但不属于联合文件系统，因此能够绕过Union File System提供一些用于持续存储或共享数据的特性：    
卷的设计目的就是数据的持久化，完全独立于容器的生存周期，因此Docker不会在容器删除时删除其挂载的数据卷。  

将docker容器内的数据保存进宿主机的磁盘中   
运行一个带有容器卷存储功能的容器实例  
```
docker run -it --privileged=true -v /宿主机绝对路径目录:/容器内目录 镜像名
```

## 四、作用
将运用与运行的环境打包镜像，run后形成容器实例运行 ，但是我们对数据的要求希望是持久化的。   
Docker容器产生的数据，如果不备份，那么当容器实例删除后，容器内的数据自然也就没有了。   
为了能保存数据在docker中我们使用卷。   

特点：  
- 数据卷可在容器之间共享或重用数据
- 卷中的更改可以直接实时生效，爽
- 数据卷中的更改不会包含在镜像的更新中
- 数据卷的生命周期一直持续到没有容器使用它为止

## 五、案例
### 5.1、宿主vs容器之间映射添加容器卷   
命令：
```
docker run -it -v /宿主机目录:/容器内目录 镜像名 /bin/bash

docker run -it --name myu3 --privileged=true -v /tmp/myHostData:/tmp/myDockerData ubuntu /bin/bash
```
查看数据卷是否挂载成功
```
docker inspect 容器ID

[root@docker130 ~]# docker inspect 99890896222b
"Mounts": [
        {
            "Type": "bind",
            "Source": "/tmp/myHostData",
            "Destination": "/tmp/myDockerData",
            "Mode": "",
            "RW": true,
            "Propagation": "rprivate"
        }
    ]
```
容器和宿主机之间数据共享
```
docker修改，主机同步获得 
主机修改，docker同步获得
docker容器stop，主机修改，docker容器重启看数据是否同步。
```

### 5.2、读写规则映射添加说明
读写(默认)rw   
rw = read + write
ro = read only : 此时如果宿主机写入内容，可以同步给容器内，容器可以读取到。
```
docker run -it --privileged=true -v /宿主机绝对路径目录:/容器内目录:rw      镜像名
docker run -it --privileged=true -v /宿主机绝对路径目录:/容器内目录:ro      镜像名

docker run -it --name myu3 --privileged=true -v /tmp/myHostData:/tmp/myDockerData:rw ubuntu /bin/bash
```

### 5.3、卷的继承和共享
1、容器1完成和宿主机的映射
```
docker run -it  --privileged=true -v /mydocker/u:/tmp --name u1 ubuntu
```
2、容器2继承容器1的卷规则
```
docker run -it  --privileged=true --volumes-from 父类  --name u2 ubuntu
```
