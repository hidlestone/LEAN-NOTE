# Arthas

## 一、Arthas(阿尔萨斯)能干什么
Arthas 是Alibaba开源的Java诊断工具，它能帮你解决以下问题：

    这个类从哪个 jar 包加载的？为什么会报各种类相关的 Exception？
    我改的代码为什么没有执行到？难道是我没 commit？分支搞错了？
    遇到问题无法在线上 debug，难道只能通过加日志再重新发布吗？
    线上遇到某个用户的数据处理有问题，但线上同样无法 debug，线下无法重现！
    是否有一个全局视角来查看系统的运行状况？
    有什么办法可以监控到JVM的实时运行状态？

## 二、下载与安装
2.1、Linux安装
下载arthas-boot(推荐)
下载arthas-boot.jar，然后用java -jar的方式启动：

    wget https://alibaba.github.io/arthas/arthas-boot.jar
    java -jar arthas-boot.jar

2.2、下载as.sh
Arthas 支持在 Linux/Unix/Mac 等平台上一键安装，请复制以下内容，并粘贴到命令行中，敲 回车 执行即可：

    curl -L https://alibaba.github.io/arthas/install.sh | sh

上述命令会下载启动脚本文件 as.sh 到当前目录，你可以放在任何地方或将其加入到 $PATH 中。
直接在shell下面执行./as.sh，就会进入交互界面。
也可以执行./as.sh -h来获取更多参数信息。

2.3、下载离线版本
Arthas最新版本为3.1.1，点我下载，得到离线包arthas-packaging-3.1.1-bin.zip
上传到服务器，解压到arthas目录

    unzip arthas-packaging-3.1.1-bin.zip -d arthas

安装启动

    cd arthas
    sh install-local.sh
    ./as.sh

或运行jar启动

    java -jar arthas-boot.jar

2.4、Widows安装
离线包arthas-packaging-3.1.1-bin.zip，解压后在根目录有 as.bat。此脚本暂时只接受一个参数 pid，即只能诊断本机上的 Java 进程，java进程pid可使用jps命令查看，启动命令如下：

    as.bat <pid>

或使用jar启动

    java -jar arthas-boot.jar

## 三、在 docker 中使用 arthas

    sudo docker ps |grep vendor
    sudo docker exec -it 9c3b90f18d20 /bin/sh

查找 arthas 的位置

    find -name '*arthas*'
    cd deploy
    ls -al

给 arthas-boot.jar 可执行权限

    chmod u+x arthas-boot.jar

运行 arthas-boot.jar
并选择其中一个jar服务(输入1，再按回车键)

    java -jar arthas-boot.jar

运行 arthas-boot.jar 查看帮助文档

    java -jar arthas-boot.jar -h

如查看 dashboard
中断当前在执行的命令，ctrl + c即可

    Dashboard

trace命令渲染和统计整个调用链路上的所有性能开销和追踪调用链路，很方便的定位性能瓶颈
trace 类名 方法名 （注：不能用子类名，即类名中要看到此方法名）

    trace com.qzing.srm.vendor.controller.StdVendorLogController  findVendorCompanyLogDtlAll

trace 帮助文档

    trace –help

查看方法耗时超过指定时间(200ms) 的列表 
trace  类名 方法名  '#cost>200’
注：trace对于lambda表达式代码支持不好，没法显示这块代码的时间。

monitor 方法

    #帮助
    monitor –help
    #监控某个类-方法
    Monitor 类 方法  
    #监控某个类-方法 5秒响应1次
    Monitor 类 方法 c 5

退出 arthas

    Quit

## 四、线上热部署

    按名称查找容器
    sudo docker ps|grep service-warehouse
    进入docker容器
    sudo docker exec -it 0f9b4f08d129 /bin/sh 
    启动arthas
    java -jar /deploy/upload/arthas-boot.jar
    更换class文件
    redefine /deploy/upload/xxx.class

Arthas 退出

    第一种
    quit 退出当前 Arthas 客户端，其他 Arthas 客户端不受影响
    第二种
    stop——和shutdown命令一致
    高能警戒, 容易把机器关闭(命令与机器命令一样)斜体样式
    shutdown——关闭 Arthas 服务端，所有 Arthas 客户端全部退出
