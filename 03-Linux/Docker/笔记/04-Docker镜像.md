# 04-Docker镜像

## 一、镜像
### 1.1、关于镜像
是一种轻量级、可执行的独立软件包，它包含运行某个软件所需的所有内容，我们把应用程序和配置依赖打包好形成一个可交付的运行环境(包括代码、运行时需要的库、环境变量和配置文件等)，这个打包好的运行环境就是image镜像文件。  

只有通过这个镜像文件才能生成Docker容器实例(类似Java中new出来一个对象)。   

### 1.2、分层的镜像
以我们的pull为例，在下载的过程中我们可以看到docker的镜像好像是在一层一层的在下载。   
![](./images/docker-32.jpg)  

### 1.3、UnionFS（联合文件系统）
UnionFS（联合文件系统）：Union文件系统（UnionFS）是一种分层、轻量级并且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层的叠加，同时可以将不同目录挂载到同一个虚拟文件系统下(unite several directories into a single virtual filesystem)。Union 文件系统是 Docker 镜像的基础。镜像可以通过分层来进行继承，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像。  

特性：一次同时加载多个文件系统，但从外面看起来，只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录  

### 1.4、Docker镜像加载原理
docker的镜像实际上由一层一层的文件系统组成，这种层级的文件系统UnionFS。   
bootfs(boot file system)主要包含bootloader和kernel, bootloader主要是引导加载kernel, Linux刚启动时会加载bootfs文件系统，在Docker镜像的最底层是引导文件系统bootfs。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核。当boot加载完成之后整个内核就都在内存中了，此时内存的使用权已由bootfs转交给内核，此时系统也会卸载bootfs。   

rootfs (root file system) ，在bootfs之上。包含的就是典型 Linux 系统中的 /dev, /proc, /bin, /etc 等标准目录和文件。rootfs就是各种不同的操作系统发行版，比如Ubuntu，Centos等等。   
![](./images/docker-33.jpg)  
![](./images/docker-34.jpg)  
对于一个精简的OS，rootfs可以很小，只需要包括最基本的命令、工具和程序库就可以了，因为底层直接用Host的kernel，自己只需要提供 rootfs 就行了。由此可见对于不同的linux发行版, bootfs基本是一致的, rootfs会有差别, 因此不同的发行版可以公用bootfs。   

### 1.5、为什么 Docker 镜像要采用这种分层结构
镜像分层最大的一个好处就是共享资源，方便复制迁移，就是为了复用。  
 
比如说有多个镜像都从相同的 base 镜像构建而来，那么 Docker Host 只需在磁盘上保存一份 base 镜像；同时内存中也只需加载一份 base 镜像，就可以为所有容器服务了。而且镜像的每一层都可以被共享。   

## 二、重点理解
Docker镜像层都是只读的，容器层是可写的 当容器启动时，一个新的可写层被加载到镜像的顶部。 这一层通常被称作“容器层”，“容器层”之下的都叫“镜像层”。

当容器启动时，一个新的可写层被加载到镜像的顶部。这一层通常被称作“容器层”，“容器层”之下的都叫“镜像层”。

所有对容器的改动 - 无论添加、删除、还是修改文件都只会发生在容器层中。只有容器层是可写的，容器层下面的所有镜像层都是只读的。  
![](./images/docker-35.jpg)  

## 三、Docker镜像commit操作
docker commit提交容器副本使之成为一个新的镜像。
```
docker commit -m="提交的描述信息" -a="作者" 容器ID 要创建的目标镜像名:[标签名]   
```

演示ubuntu安装vim  
1、从Hub上下载ubuntu镜像到本地并成功运行。原始的默认Ubuntu镜像是不带着vim命令的。  
2、外网连通的情况下，安装vim。  
docker容器内执行上述两条命令：  
```
apt-get update
apt-get -y install vim
```
3、安装完成后，commit自己的新镜像
```
docker commit -m="提交的描述信息" -a="作者" 容器ID 要创建的目标镜像名:[标签名]  
[root@docker130 ~]# docker commit -m="add vim" -a="zhuangpf" 1df21dbc89e3 zhuangpf/myubuntu:1.0
sha256:164f0c138b0e141701e236e3ae1de7c2bb86a492a659739f5d6752fb5f9c48b8
```
4、启动我们的新镜像并和原来的对比
```
[root@docker130 ~]# docker images
REPOSITORY          TAG       IMAGE ID       CREATED              SIZE
zhuangpf/myubuntu   1.0       fc1d6e70130d   About a minute ago   181MB
ubuntu              latest    ba6acccedd29   14 months ago        72.8MB
```

总结：   
Docker中的镜像分层，支持通过扩展现有镜像，创建新的镜像。类似Java继承于一个Base基础类，自己再按需扩展。  
新镜像是从 base 镜像一层一层叠加生成的。每安装一个软件，就在现有镜像的基础上增加一层。  
![](./images/docker-36.jpg)  

