# 01-Flink简介
## 一、Flink 简介 
### 1.1、初识Flink 
Flink 起源于 Stratosphere 项目，Stratosphere 是在 2010~2014 年由 3 所地处柏林的大学和欧洲的一些其他的大学共同进行的研究项目，2014 年 4 月 Stratosphere 的代码被复制并捐赠给了 Apache 软件基金会，参加这个孵化项目的初始成员是Stratosphere 系统的核心开发人员，2014 年 12 月，Flink 一跃成为 Apache 软件基金会的顶级项目。    

在德语中，Flink 一词表示快速和灵巧，项目采用一只松鼠的彩色图案作为 logo，这不仅是因为松鼠具有快速和灵巧的特点，还因为柏林的松鼠有一种迷人的红棕色，而 Flink 的松鼠 logo 拥有可爱的尾巴，尾巴的颜色与 Apache 软件基金会的 logo 颜色相呼应，也就是说，这是一只 Apache 风格的松鼠。   

Flink 项目的理念是：“Apache Flink 是为分布式、高性能、随时可用以及准确的流处理应用程序打造的开源流处理框架”。 

Apache Flink 是一个框架和分布式处理引擎，用于对无界和有界数据流进行有状态计算。Flink 被设计在所有常见的集群环境中运行，以内存执行速度和任意规模来执行计算。 

![](./images/flink-01.png)  

### 1.2、Flink的重要特点  
#### 1.2.1、事件驱动型(Event-driven)
事件驱动型应用是一类具有状态的应用，它从一个或多个事件流提取数据，并根据到来的事件触发计算、状态更新或其他外部动作。比较典型的就是以 kafka 为代表的消息队列几乎都是事件驱动型应用。 

与之不同的就是 SparkStreaming 微批次，如图：   
![](./images/flink-02.png)   
事件驱动型： 
![](./images/flink-03.png)   

#### 1.2.2、流与批的世界观 
批处理的特点是有界、持久、大量，非常适合需要访问全套记录才能完成的计算工作，一般用于离线统计。 

流处理的特点是无界、实时,  无需针对整个数据集执行操作，而是对通过系统传输的每个数据项执行操作，一般用于实时统计。 

在 spark 的世界观中，一切都是由批次组成的，离线数据是一个大批次，而实时数据是由一个一个无限的小批次组成的。 

而在 flink 的世界观中，一切都是由流组成的，离线数据是有界限的流，实时数据是一个没有界限的流，这就是所谓的有界流和无界流。 

无界数据流：无界数据流有一个开始但是没有结束，它们不会在生成时终止并提供数据，必须连续处理无界流，也就是说必须在获取后立即处理 event。对于无界数据流我们无法等待所有数据都到达，因为输入是无界的，并且在任何时间点都不会完成。处理无界数据通常要求以特定顺序（例如事件发生的顺序）获取 event，以便能够推断结果完整性。 

有界数据流：有界数据流有明确定义的开始和结束，可以在执行任何计算之前通过获取所有数据来处理有界流，处理有界流不需要有序获取，因为可以始终对有界数据集进行排序，有界流的处理也称为批处理。 
![](./images/flink-04.png)   
这种以流为世界观的架构，获得的最大好处就是具有极低的延迟。   

#### 1.2.3、分层 api 
![](./images/flink-05.png)   

最底层级的抽象仅仅提供了有状态流，它将通过过程函数（Process Function）被嵌入到 DataStream API 中。底层过程函数（Process Function） 与 DataStream API 相集成，使其可以对某些特定的操作进行底层的抽象，它允许用户可以自由地处理来自一个或多个数据流的事件，并使用一致的容错的状态。除此之外，用户可以注册事件时间并处理时间回调，从而使程序可以处理复杂的计算。 

实际上，大多数应用并不需要上述的底层抽象，而是针对核心 API（Core APIs）进行编程，比如 DataStream API（有界或无界流数据）以及 DataSet API（有界数据集）。这些 API 为数据处理提供了通用的构建模块，比如由用户定义的多种形式的转换（transformations），连接（joins），聚合（aggregations），窗口操作（windows）等等。DataSet API 为有界数据集提供了额外的支持，例如循环与迭代。这些 API处理的数据类型以类（classes）的形式由各自的编程语言所表示。 

Table API 是以表为中心的声明式编程，其中表可能会动态变化（在表达流数据时）。Table API 遵循（扩展的）关系模型：表有二维数据结构（schema）（类似于关系数据库中的表），同时API 提供可比较的操作，例如 select、project、join、group-by、aggregate 等。Table API 程序声明式地定义了什么逻辑操作应该执行，而不是准确地确定这些操作代码的看上去如何。 

尽管 Table API 可以通过多种类型的用户自定义函数（UDF）进行扩展，其仍不如核心 API 更具表达能力，但是使用起来却更加简洁（代码量更少）。除此之外，Table API 程序在执行之前会经过内置优化器进行优化。 

你可以在表与 DataStream/DataSet 之间无缝切换，以允许程序将 Table API 与 DataStream 以及 DataSet 混合使用。 

Flink 提供的最高层级的抽象是 SQL 。这一层抽象在语法与表达能力上与 Table API 类似，但是是以 SQL 查询表达式的形式表现程序。SQL 抽象与 Table API交互密切，同时 SQL 查询可以直接在 Table API 定义的表上执行。 

目前 Flink 作为批处理还不是主流，不如 Spark 成熟，所以 DataSet 使用的并不是很多。Flink Table API 和 Flink SQL 也并不完善，大多都由各大厂商自己定制。所以我们主要学习 DataStream API 的使用。实际上 Flink 作为最接近 Google DataFlow模型的实现，是流批统一的观点，所以基本上使用 DataStream 就可以了。

Flink 几大模块 
- Flink Table & SQL(还没开发完) 
- Flink Gelly(图计算) 
- Flink CEP(复杂事件处理) 


## 二、快速上手 
### 2.1、搭建 maven工程 FlinkTutorial 
pom 文件 
```xml
<?xml version="1.0" encoding="UTF-8"?> 
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
http://maven.apache.org/xsd/maven-4.0.0.xsd"> 
    <modelVersion>4.0.0</modelVersion> 
    <groupId>com.atguigu.flink</groupId> 
    <artifactId>FlinkTutorial</artifactId> 
    <version>1.0-SNAPSHOT</version> 
 
    <dependencies> 
        <dependency> 
            <groupId>org.apache.flink</groupId> 
            <artifactId>flink-java</artifactId> 
            <version>1.10.1</version> 
        </dependency> 
        <dependency> 
            <groupId>org.apache.flink</groupId> 
            <artifactId>flink-streaming-java_2.12</artifactId> 
            <version>1.10.1</version> 
        </dependency> 
    </dependencies> 
</project> 
```

### 2.2、批处理wordcount 
```
public class WordCount { 
    public static void main(String[] args) throws Exception {
        // 创建执行环境
 
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment(); 
 
        // 从文件中读取数据
 
        String inputPath = "hello.txt"; 
        DataSet<String> inputDataSet = env.readTextFile(inputPath); 
 
        // 空格分词打散之后，对单词进行 groupby分组，然后用 sum进行聚合
 
        DataSet<Tuple2<String, Integer>> wordCountDataSet = 
                inputDataSet.flatMap(new MyFlatMapper()) 
                .groupBy(0) 
                .sum(1); 
 
        // 打印输出
 
        wordCountDataSet.print(); 
    } 
 
    public static class MyFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> { 
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception { 
            String[] words = value.split(" "); 
            for (String word : words) { 
                out.collect(new Tuple2<String, Integer>(word, 1)); 
            } 
        } 
    } 
} 
```

### 2.3、流处理wordcount 
```
public class StreamWordCount {
    public static void main(String[] args) throws Exception{
        // 创建流处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//        env.disableOperatorChaining();

//        // 从文件中读取数据
//        String inputPath = "D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\hello.txt";
//        DataStream<String> inputDataStream = env.readTextFile(inputPath);

        // 用parameter tool工具从程序启动参数中提取配置项

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String host = parameterTool.get("host");
        int port = parameterTool.getInt("port");

        // 从socket文本流读取数据
        DataStream<String> inputDataStream = env.socketTextStream(host, port);


        // 基于数据流进行转换计算
        DataStream<Tuple2<String, Integer>> resultStream = inputDataStream.flatMap(new WordCount.MyFlatMapper())
                .keyBy(0)
                .sum(1);

        resultStream.print().setParallelism(1);

        // 执行任务
        env.execute();
    }
}
```
测试——在 linux 系统中用 netcat 命令进行发送测试。   
```
nc -lk  7777 
```

## 三、Flink部署
### 3.1、Standalone 模式 
#### 3.1.1、安装
解压缩  flink-1.10.1-bin-scala_2.12.tgz，进入 conf 目录中。      
```
tar -zxvf /opt/software/flink-1.10.1-bin-scala_2.12.tgz -C /opt/module/
```
1、修改flink/conf/flink-conf.yaml文件   
```
# The external address of the host on which the JobManager runs and can be
# reached by the TaskManagers and any clients which want to connect. This setting
# is only used in Standalone mode and may be overwritten on the JobManager side
# by specifying the --host <hostname> parameter of the bin/jobmanager.sh executable.
# In high availability mode, if you use the bin/start-cluster.sh script and setup
# the conf/masters file, this will be taken care of automatically. Yarn/Mesos
# automatically configure the host name based on the hostname of the node where the
# JobManager runs.

jobmanager.rpc.address: hadoop102
```
2、修改/conf/slaves文件：   
```
hadoop103
hadoop104
```
3、分发到另外两台机器上   
```
xsync flink-1.10.0
```
4、启动
![](./images/flink-07.png)     
访问 http://localhost:8081 可以对 flink 集群和任务进行监控管理。   

#### 3.1.2、提交任务 
1) 准备数据文件 （如果需要）    
2) 把含数据文件的文件夹，分发到taskmanage 机器中    
如果从文件中读取数据，由于是从本地磁盘读取，实际任务会被分发到taskmanage 的机器中，所以要把目标文件分发。    
3) 执行任务   
```
./flink run -c com.atguigu.wc.StreamWordCount –p 2 FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar --host lcoalhost –port 7777  
```
![](./images/flink-08.png)     

4) 查看计算结果   
注意：如果输出到控制台，应该在 taskmanager 下查看；如果计算结果输出到文件，同样会保存到 taskmanage 的机器下，不会在 jobmanage 下。     

5) 在webui控制台查看计算过程    
![](./images/flink-09.png)    

### 3.2、Yarn 模式 
以 Yarn 模式部署 Flink 任务时，要求Flink 是有 Hadoop 支持的版本，Hadoop环境需要保证版本在 2.2 以上，并且集群中安装有 HDFS 服务。     

#### 3.2.1、Flink on Yarn 
Flink 提供了两种在yarn上运行的模式，分别为 Session-Cluster 和 Per-Job-Cluster模式。 

1) Session-cluster 模式：    
![](./images/flink-10.png)   
Session-Cluster 模式需要先启动集群，然后再提交作业，接着会向 yarn 申请一块空间后，资源永远保持不变。如果资源满了，下一个作业就无法提交，只能等到yarn 中的其中一个作业执行完成后，释放了资源，下个作业才会正常提交。所有作业共享 Dispatcher 和 ResourceManager；共享资源；适合规模小执行时间短的作业。   

在 yarn 中初始化一个 flink 集群，开辟指定的资源，以后提交任务都向这里提交。这个 flink 集群会常驻在 yarn 集群中，除非手工停止。    

2) Per-Job-Cluster 模式：    
![](./images/flink-11.png)   
一个 Job 会对应一个集群，每提交一个作业会根据自身的情况，都会单独向 yarn申请资源，直到作业执行完成，一个作业的失败与否并不会影响下一个作业的正常提交和运行。独享 Dispatcher 和 ResourceManager，按需接受资源申请；适合规模大长时间运行的作业。    

每次提交都会创建一个新的 flink 集群，任务之间互相独立，互不影响，方便管理。任务执行完成之后创建的集群也会消失。   

#### 3.2.2、Session Cluster 
1) 启动hadoop集群（略）    

2) 启动yarn-session
```
./yarn-session.sh -n 2 -s 2 -jm 1024 -tm 1024 -nm test -d 
```
其中： 
```
-n(--container)：TaskManager 的数量。 
-s(--slots)： 每个 TaskManager 的 slot 数量，默认一个 slot 一个 core，默认每个
taskmanager 的 slot 的个数为 1，有时可以多一些 taskmanager，做冗余。 
-jm：JobManager 的内存（单位 MB)。 
-tm：每个 taskmanager 的内存（单位 MB)。 
-nm：yarn 的 appName(现在 yarn 的 ui 上的名字)。  
-d：后台执行。 
```

3) 执行任务
```
./flink run -c com.atguigu.wc.StreamWordCount  FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar --host lcoalhost –port 7777 
```

4) 去yarn控制台查看任务状态
![](./images/flink-12.png)   

5) 取消yarn-session 
```
yarn application --kill application_1577588252906_0001 
```

#### 3.2.3、Per Job Cluster 
1) 启动hadoop集群（略）  

2) 不启动yarn-session，直接执行job
```
./flink run –m yarn-cluster -c com.atguigu.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar --host lcoalhost –port 7777 
```

### 3.3、Kubernetes 部署 
容器化部署时目前业界很流行的一项技术，基于 Docker 镜像运行能够让用户更加方便地对应用进行管理和运维。容器管理工具中最为流行的就是 Kubernetes（k8s），而 Flink 也在最近的版本中支持了 k8s 部署模式。    

1）搭建Kubernetes集群（略）  
2）配置各组件的yaml文件    
在 k8s 上构建 Flink Session Cluster，需要将 Flink 集群的组件对应的 docker 镜像分别在 k8s 上启动，包括 JobManager、TaskManager、JobManagerService 三个镜像服务。每个镜像服务都可以从中央镜像仓库中获取。    

3）启动Flink Session Cluster
```
// 启动jobmanager-service 服务
kubectl create -f jobmanager-service.yaml 
// 启动jobmanager-deployment服务
kubectl create -f jobmanager-deployment.yaml 
// 启动taskmanager-deployment服务
kubectl create -f taskmanager-deployment.yaml 
```

4 ）访问Flink UI页面   
集群启动后，就可以通过 JobManagerServicers 中配置的 WebUI 端口，用浏览器输入以下 url 来访问 Flink UI 页面了：     
http://{JobManagerHost:Port}/api/v1/namespaces/default/services/flink-jobmanager:ui/proxy    

## 四、Flink 运行架构 
### 4.1、Flink 运行时的组件 
Flink 运行时架构主要包括四个不同的组件，它们会在运行流处理应用程序时协同工作：作业管理器（JobManager）、资源管理器（ResourceManager）、任务管理器（TaskManager），以及分发器（Dispatcher）。因为 Flink 是用 Java 和 Scala 实现的，所以所有组件都会运行在Java 虚拟机上。每个组件的职责如下：   

作业管理器（JobManager）    
控制一个应用程序执行的主进程，也就是说，每个应用程序都会被一个不同的JobManager 所控制执行。JobManager 会先接收到要执行的应用程序，这个应用程序会包括：作业图（JobGraph）、逻辑数据流图（logical dataflow graph）和打包了所有的类、库和其它资源的 JAR 包。JobManager 会把 JobGraph 转换成一个物理层面的数据流图，这个图被叫做“执行图”（ExecutionGraph），包含了所有可以并发执行的任务。JobManager 会向资源管理器（ResourceManager）请求执行任务必要的资源，也就是任务管理器（TaskManager）上的插槽（slot）。一旦它获取到了足够的资源，就会将执行图分发到真正运行它们的TaskManager 上。而在运行过程中，JobManager 会负责所有需要中央协调的操作，比如说检查点（checkpoints）的协调。    

资源管理器（ResourceManager）    
主要负责管理任务管理器（TaskManager）的插槽（slot），TaskManger 插槽是 Flink 中定义的处理资源单元。Flink 为不同的环境和资源管理工具提供了不同资源管理器，比如YARN、Mesos、K8s，以及 standalone 部署。当 JobManager 申请插槽资源时，ResourceManager会将有空闲插槽的 TaskManager 分配给 JobManager。如果 ResourceManager 没有足够的插槽来满足 JobManager 的请求，它还可以向资源提供平台发起会话，以提供启动 TaskManager进程的容器。另外，ResourceManager 还负责终止空闲的 TaskManager，释放计算资源。    

任务管理器（TaskManager）    
Flink 中的工作进程。通常在 Flink 中会有多个 TaskManager 运行，每一个 TaskManager都包含了一定数量的插槽（slots）。插槽的数量限制了 TaskManager 能够执行的任务数量。启动之后，TaskManager 会向资源管理器注册它的插槽；收到资源管理器的指令后，TaskManager 就会将一个或者多个插槽提供给 JobManager 调用。JobManager 就可以向插槽分配任务（tasks）来执行了。在执行过程中，一个 TaskManager 可以跟其它运行同一应用程序的 TaskManager 交换数据。   

分发器（Dispatcher）   
可以跨作业运行，它为应用提交提供了 REST 接口。当一个应用被提交执行时，分发器就会启动并将应用移交给一个 JobManager。由于是 REST 接口，所以 Dispatcher 可以作为集群的一个 HTTP 接入点，这样就能够不受防火墙阻挡。Dispatcher 也会启动一个 Web UI，用来方便地展示和监控作业执行的信息。Dispatcher 在架构中可能并不是必需的，这取决于应用提交运行的方式。   

### 4.2、任务提交流程 
我们来看看当一个应用提交执行时，Flink 的各个组件是如何交互协作的：   
![](./images/flink-13.png)    
上图是从一个较为高层级的视角，来看应用中各组件的交互协作。如果部署的集群环境不同（例如 YARN，Mesos，Kubernetes，standalone 等），其中一些步骤可以被省略，或是有些组件会运行在同一个 JVM 进程中。    

具体地，如果我们将 Flink 集群部署到 YARN 上，那么就会有如下的提交流程：    
![](./images/flink-14.png)    

Flink 任务提交后，Client 向 HDFS 上传 Flink 的 Jar 包和配置，之后向 Yarn ResourceManager 提交任务，ResourceManager 分配 Container 资源并通知对应的NodeManager 启动 ApplicationMaster，ApplicationMaster 启动后加载 Flink 的 Jar 包和配置构建环境，然后启动 JobManager，之后 ApplicationMaster 向 ResourceManager申请资源启动 TaskManager ， ResourceManager 分配 Container 资 源 后 ， 由ApplicationMaster 通 知 资 源 所 在 节 点 的 NodeManager 启动 TaskManager ，NodeManager 加载 Flink 的 Jar 包和配置构建环境并启动 TaskManager，TaskManager启动后向 JobManager 发送心跳包，并等待 JobManager 向其分配任务。    

### 4.3、任务调度原理 
![](./images/flink-15.png)    
客户端不是运行时和程序执行的一部分，但它用于准备并发送dataflow(JobGraph)给 Master(JobManager)，然后，客户端断开连接或者维持连接以等待接收计算结果。    

当 Flink集群启动后，首先会启动一个JobManger 和一个或多个的 TaskManager。由 Client 提交任务给 JobManager，JobManager 再调度任务到各个 TaskManager 去执行，然后 TaskManager 将心跳和统计信息汇报给 JobManager。TaskManager 之间以流的形式进行数据的传输。上述三者均为独立的 JVM 进程。    

Client 为提交 Job 的客户端，可以是运行在任何机器上（与 JobManager 环境连通即可）。提交 Job 后，Client 可以结束进程（Streaming 的任务），也可以不结束并等待结果返回。   

JobManager 主要负责调度 Job 并协调 Task 做 checkpoint，职责上很像 Storm 的 Nimbus。从 Client 处接收到 Job 和 JAR 包等资源后，会生成优化后的执行计划，并以 Task 的单元调度到各个 TaskManager 去执行。   

TaskManager 在启动的时候就设置好了槽位数（Slot），每个 slot 能启动一个 Task，Task 为线程。从 JobManager 处接收需要部署的 Task，部署启动后，与自己的上游建立 Netty 连接，接收数据并处理。 

#### 4.3.1、TaskManger 与 Slots  
Flink 中每一个 worker(TaskManager)都是一个 JVM 进程，它可能会在独立的线程上执行一个或多个 subtask。为了控制一个 worker 能接收多少个 task，worker 通过 task slot 来进行控制（一个 worker 至少有一个 task slot）。    

每个 task slot 表示 TaskManager 拥有资源的一个固定大小的子集。假如一个TaskManager 有三个 slot，那么它会将其管理的内存分成三份给各个 slot。资源 slot化意味着一个 subtask 将不需要跟来自其他 job 的 subtask 竞争被管理的内存，取而代之的是它将拥有一定数量的内存储备。需要注意的是，这里不会涉及到 CPU 的隔离，slot 目前仅仅用来隔离 task 的受管理的内存。    
![](./images/flink-16.png)    
![](./images/flink-17.png)    

默认情况下，Flink 允许子任务共享 slot，即使它们是不同任务的子任务（前提是它们来自同一个 job）。 这样的结果是，一个 slot 可以保存作业的整个管道。    

Task Slot 是静态的概念，是指 TaskManager 具有的并发执行能力，可以通过参数 taskmanager.numberOfTaskSlots 进行配置；而并行度 parallelism 是动态概念，即 TaskManager 运行程序时实际使用的并发能力，可以通过参数 parallelism.default进行配置。   

也就是说，假设一共有 3 个 TaskManager，每一个 TaskManager 中的分配 3 个TaskSlot，也就是每个 TaskManager 可以接收 3 个 task，一共 9 个 TaskSlot，如果我们设置 parallelism.default=1，即运行程序默认的并行度为 1，9 个 TaskSlot 只用了 1个，有 8 个空闲，因此，设置合适的并行度才能提高效率。    
![](./images/flink-18.png)   
![](./images/flink-19.png)   

#### 4.3.2、程序与数据流（DataFlow） 
![](./images/flink-20.png)   

所有的 Flink 程序都是由三部分组成的：  Source 、Transformation 和 Sink。   
Source 负责读取数据源，Transformation 利用各种算子进行处理加工，Sink 负责输出。    

在运行时，Flink 上运行的程序会被映射成“逻辑数据流”（dataflows），它包含了这三部分。每一个 dataflow 以一个或多个 sources 开始以一个或多个 sinks 结束。dataflow 类似于任意的有向无环图（DAG）。在大部分情况下，程序中的转换运算（transformations）跟 dataflow 中的算子（operator）是一一对应的关系，但有时候，一个 transformation 可能对应多个 operator。    
![](./images/flink-21.png)   

#### 4.3.3、执行图（ExecutionGraph）
由 Flink 程序直接映射成的数据流图是 StreamGraph，也被称为逻辑流图，因为它们表示的是计算逻辑的高级视图。为了执行一个流处理程序，Flink 需要将逻辑流图转换为物理数据流图（也叫执行图），详细说明程序的执行方式。    

Flink 中的执行图可以分成四层：StreamGraph -> JobGraph -> ExecutionGraph -> 物理执行图。      
- StreamGraph：是根据用户通过 Stream API 编写的代码生成的最初的图。用来表示程序的拓扑结构。    
- JobGraph：StreamGraph 经过优化后生成了 JobGraph，提交给 JobManager 的数据结构。主要的优化为，将多个符合条件的节点 chain 在一起作为一个节点，这样可以减少数据在节点之间流动所需要的序列化/反序列化/传输消耗。    
- ExecutionGraph ：JobManager 根据 JobGraph 生成 ExecutionGraph 。ExecutionGraph 是 JobGraph 的并行化版本，是调度层最核心的数据结构。    
- 物理执行图：JobManager 根据 ExecutionGraph 对 Job 进行调度后，在各个TaskManager 上部署 Task 后形成的“图”，并不是一个具体的数据结构。    

![](./images/flink-22.png)   

#### 4.3.4、并行度（Parallelism） 
Flink 程序的执行具有并行、分布式的特性。    

在执行过程中，一个流（stream）包含一个或多个分区（stream partition），而每一个算子（operator）可以包含一个或多个子任务（operator subtask），这些子任务在不同的线程、不同的物理机或不同的容器中彼此互不依赖地执行。    

一个特定算子的子任务（subtask）的个数被称之为其并行度（parallelism）。一般情况下，一个流程序的并行度，可以认为就是其所有算子中最大的并行度。一个程序中，不同的算子可能具有不同的并行度。    
![](./images/flink-23.png)   

Stream 在算子之间传输数据的形式可以是 one-to-one(forwarding)的模式也可以是 redistributing 的模式，具体是哪一种形式，取决于算子的种类。    

One-to-one：stream(比如在 source 和 map operator 之间)维护着分区以及元素的顺序。那意味着 map 算子的子任务看到的元素的个数以及顺序跟 source 算子的子任务生产的元素的个数、顺序相同，map、fliter、flatMap 等算子都是 one-to-one 的对应关系。   

类似于 spark 中的窄依赖   
Redistributing：stream(map()跟 keyBy/window 之间或者 keyBy/window 跟 sink之间)的分区会发生改变。每一个算子的子任务依据所选择的 transformation 发送数据到不同的目标任务。例如，keyBy() 基于 hashCode 重分区、broadcast 和 rebalance会随机重新分区，这些算子都会引起 redistribute 过程，而 redistribute 过程就类似于Spark 中的 shuffle 过程。    

类似于 spark 中的宽依赖    

#### 4.3.5、任务链（Operator Chains） 
**相同并行度的 one to one 操作**，Flink 这样相连的算子链接在一起形成一个 task， 原来的算子成为里面的一部分。将算子链接成 task 是非常有效的优化：它能减少线 程之间的切换和基于缓存区的数据交换，在减少时延的同时提升吞吐量。链接的行 为可以在编程 API 中进行指定。    
![](./images/flink-24.png)   

## 五、Flink 流处理 API 
![](./images/flink-25.png)   

### 5.1、Environment 
#### 5.1.1 getExecutionEnvironment 
创建一个执行环境，表示当前执行程序的上下文。 如果程序是独立调用的，则 此方法返回本地执行环境；如果从命令行客户端调用程序以提交到集群，则此方法 返回此集群的执行环境，也就是说，getExecutionEnvironment 会根据查询运行的方 式决定返回什么样的运行环境，是最常用的一种创建执行环境的方式。    
```
ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment(); 

StreamExecutionEnvironment env = 
StreamExecutionEnvironment.getExecutionEnvironment(); 
```
如果没有设置并行度，会以 flink-conf.yaml 中的配置为准，默认是 1。    
![](./images/flink-26.png)   

#### 5.1.2、createLocalEnvironment 
返回本地执行环境，需要在调用时指定默认的并行度。 
```
LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(1); 
```

#### 5.1.3、createRemoteEnvironment 
返回集群执行环境，将 Jar 提交到远程服务器。需要在调用时指定 JobManager 的 IP 和端口号，并指定要在集群中运行的 Jar 包。 
```
StreamExecutionEnvironment env = 
StreamExecutionEnvironment.createRemoteEnvironment("jobmanage-hostname", 6123, 
"YOURPATH//WordCount.jar"); 
```

### 5.2、Source  
#### 5.2.1 从集合读取数据 
```
public class SourceTest1_Collection {
    public static void main(String[] args) throws Exception{
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从集合中读取数据
        DataStream<SensorReading> dataStream = env.fromCollection(Arrays.asList(
                new SensorReading("sensor_1", 1547718199L, 35.8),
                new SensorReading("sensor_6", 1547718201L, 15.4),
                new SensorReading("sensor_7", 1547718202L, 6.7),
                new SensorReading("sensor_10", 1547718205L, 38.1)
        ));

        DataStream<Integer> integerDataStream = env.fromElements(1, 2, 4, 67, 189);

        // 打印输出
        dataStream.print("data");
        integerDataStream.print("int");

        // 执行
        env.execute();
    }
}
```

#### 5.2.2、从文件读取数据 
```
DataStream<String> dataStream = env.readTextFile("YOUR_FILE_PATH "); 
```

#### 5.2.3、以 kafka 消息队列的数据作为来源 
需要引入 kafka 连接器的依赖：   
```
<! -- https://mvnrepository.com/artifa ct/org.apache.flink/flink - connector - kafka - 0.11 -- > 
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka-0.11_2.12</artifactId>
    <version>1.10.1</version>
</dependency>
```
具体代码如下：   
```
// kafka
配置项 
Properties properties = new Properties(); 
properties.setProperty("bootstrap.servers", "localhost:9092"); 
properties.setProperty("group.id", "consumer-group"); 
properties.setProperty("key.deserializer", 
"org.apache.kafka.common.serialization.StringDeserializer"); 
properties.setProperty("value.deserializer", 
"org.apache.kafka.common.serialization.StringDeserializer"); 
properties.setProperty("auto.offset.reset", "latest"); 
 
 
// 从 kafka读取数据 
DataStream<String> dataStream = env.addSource( new 
FlinkKafkaConsumer011<String>("sensor", new SimpleStringSchema(), properties));
```

#### 5.2.4、自定义 Source 
除了以上的 source 数据来源，我们还可以自定义 source。需要做的，只是传入一个 SourceFunction 就可以。具体调用如下：    
```
DataStream<SensorReading> dataStream = env.addSource( new MySensor());
```
我们希望可以随机生成传感器数据，MySensorSource 具体的代码实现如下：    
```
public class SourceTest4_UDF {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读取数据
        DataStream<SensorReading> dataStream = env.addSource( new MySensorSource() );

        // 打印输出
        dataStream.print();

        env.execute();
    }

    // 实现自定义的SourceFunction
    public static class MySensorSource implements SourceFunction<SensorReading>{
        // 定义一个标识位，用来控制数据的产生
        private boolean running = true;

        @Override
        public void run(SourceContext<SensorReading> ctx) throws Exception {
            // 定义一个随机数发生器
            Random random = new Random();

            // 设置10个传感器的初始温度
            HashMap<String, Double> sensorTempMap = new HashMap<>();
            for( int i = 0; i < 10; i++ ){
                sensorTempMap.put("sensor_" + (i+1), 60 + random.nextGaussian() * 20);
            }

            while (running){
                for( String sensorId: sensorTempMap.keySet() ){
                    // 在当前温度基础上随机波动
                    Double newtemp = sensorTempMap.get(sensorId) + random.nextGaussian();
                    sensorTempMap.put(sensorId, newtemp);
                    ctx.collect(new SensorReading(sensorId, System.currentTimeMillis(), newtemp));
                }
                // 控制输出频率
                Thread.sleep(1000L);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}
```

### 5.3、Transform 
转换算子  

#### 5.3.1、map 
![](./images/flink-27.png)   
```
DataStream<Integer> mapStram = dataStream.map(new MapFunction<String, Integer>() { 
    public Integer map(String value) throws Exception { 
        return value.length(); 
    } 
}); 
```

#### 5.3.2、flatMap 
```
DataStream<String> flatMapStream = dataStream.flatMap(new FlatMapFunction<String, 
String>() { 
    public void flatMap(String value, Collector<String> out) throws Exception { 
        String[] fields = value.split(",");  
        for( String field: fields ) 
            out.collect(field); 
 } 
}); 
```
 
#### 5.3.3、Filter 
![](./images/flink-28.png)   
```
DataStream<Interger> filterStream = dataStream.filter(new FilterFunction<String>() 
{ 
    public boolean filter(String value) throws Exception { 
        return value == 1; 
    } 
}); 
```

#### 5.3.4、KeyBy 
![](./images/flink-29.png)   
DataStream → KeyedStream：逻辑地将一个流拆分成不相交的分区，每个分区包含具有相同 key 的元素，在内部以 hash 的形式实现的。 

#### 5.3.5、滚动聚合算子（Rolling Aggregation） 
这些算子可以针对 KeyedStream 的每一个支流做聚合。    
```
sum() 
min() 
max() 
minBy() 
maxBy() 
```

#### 5.3.6、Reduce 
KeyedStream → DataStream：一个分组数据流的聚合操作，合并当前的元素和上次聚合的结果，产生一个新的值，返回的流中包含每一次聚合的结果，而不是只返回最后一次聚合的最终结果。 
```
DataStream<String> inputStream = env.readTextFile("sensor.txt"); 
 
        // 转换成 SensorReading类型 
        DataStream<SensorReading> dataStream = inputStream.map(new MapFunction<String, SensorReading>() { 
            public SensorReading map(String value) throws Exception { 
                String[] fileds = value.split(","); 
                return new SensorReading(fileds[0], new Long(fileds[1]), new Double(fileds[2])); 
            } 
        }); 
 
        // 分组         
		KeyedStream<SensorReading, Tuple> keyedStream = dataStream.keyBy("id"); 
 
        // reduce聚合，取最小的温度值，并输出当前的时间戳 
        DataStream<SensorReading> reduceStream = keyedStream.reduce(new ReduceFunction<SensorReading>() { 
            @Override 
            public SensorReading reduce(SensorReading value1, SensorReading value2) throws Exception { 
                return new SensorReading( 
                        value1.getId(), 
                        value2.getTimestamp(), 
                        Math.min(value1.getTemperature(), value2.getTemperature())); 
            } 
        }); 
```

#### 5.3.7、Split 和 Select 
![](./images/flink-30.png)    
DataStream → SplitStream：根据某些特征把一个 DataStream 拆分成两个或者多个 DataStream。    

![](./images/flink-31.png)    
SplitStream→DataStream：从一个 SplitStream 中获取一个或者多个DataStream。    
需求：传感器数据按照温度高低（以 30 度为界），拆分成两个流。    
```
SplitStream<SensorReading> splitStream = dataStream.split(new 
OutputSelector<SensorReading>() { 
    @Override 
    public Iterable<String> select(SensorReading value) { 
        return (value.getTemperature() > 30) ? Collections.singletonList("high") : 
Collections.singletonList("low"); 
    } 
}); 
 
DataStream<SensorReading> highTempStream = splitStream.select("high"); 
DataStream<SensorReading> lowTempStream = splitStream.select("low"); 
DataStream<SensorReading> allTempStream = splitStream.select("high", "low"); 
```

#### 5.3.8、Connect 和 CoMap 
![](./images/flink-32.png)    

DataStream,DataStream → ConnectedStreams：连接两个保持他们类型的数据流，两个数据流被 Connect 之后，只是被放在了一个同一个流中，内部依然保持各自的数据和形式不发生任何变化，两个流相互独立。   

CoMap,CoFlatMap    
![](./images/flink-33.png)    
ConnectedStreams → DataStream：作用于 ConnectedStreams 上，功能与 map和 flatMap 一样，对 ConnectedStreams 中的每一个 Stream 分别进行 map 和 flatMap处理。    
```
// 合流 connect 
DataStream<Tuple2<String, Double>> warningStream = highTempStream.map(new 
MapFunction<SensorReading, Tuple2<String, Double>>() { 
    @Override 
     public Tuple2<String, Double> map(SensorReading value) throws Exception { 
            return new Tuple2<>(value.getId(), value.getTemperature()); 
        } 
    }); 
    ConnectedStreams<Tuple2<String, Double>, SensorReading> connectedStreams = 
    warningStream.connect(lowTempStream); 
     
    DataStream<Object> resultStream = connectedStreams.map(new 
    CoMapFunction<Tuple2<String,Double>, SensorReading, Object>() { 
        @Override 
        public Object map1(Tuple2<String, Double> value) throws Exception { 
            return new Tuple3<>(value.f0, value.f1, "warning"); 
        } 
     
        @Override 
        public Object map2(SensorReading value) throws Exception { 
            return new Tuple2<>(value.getId(), "healthy"); 
        } 
}); 
```

#### 5.3.9、Union 
![](./images/flink-34.png)     
DataStream → DataStream：对两个或者两个以上的 DataStream 进行 union 操作，产生一个包含所有 DataStream 元素的新 DataStream。   
```
DataStream<SensorReading> unionStream = highTempStream.union(lowTempStream); 
```

Connect 与 Union 区别：   
1、Union 之前两个流的类型必须是一样，Connect 可以不一样，在之后的 coMap中再去调整成为一样的。   
2、Connect 只能操作两个流，Union 可以操作多个。   

### 5.4、支持的数据类型 
Flink 流应用程序处理的是以数据对象表示的事件流。所以在 Flink 内部，我们需要能够处理这些对象。它们需要被序列化和反序列化，以便通过网络传送它们；或者从状态后端、检查点和保存点读取它们。为了有效地做到这一点，Flink 需要明确知道应用程序所处理的数据类型。Flink 使用类型信息的概念来表示数据类型，并为每个数据类型生成特定的序列化器、反序列化器和比较器。   

Flink 还具有一个类型提取系统，该系统分析函数的输入和返回类型，以自动获取类型信息，从而获得序列化器和反序列化器。但是，在某些情况下，例如 lambda函数或泛型类型，需要显式地提供类型信息，才能使应用程序正常工作或提高其性能。   

Flink 支持 Java 和 Scala 中所有常见数据类型。使用最广泛的类型有以下几种。

#### 5.4.1、基础数据类型 
Flink 支持所有的 Java 和 Scala 基础数据类型，Int, Double, Long, String, …    
```
DataStream<Integer> numberStream = env.fromElements(1, 2, 3, 4); 
numberStream.map(data -> data * 2); 
```

#### 5.4.2、Java 和 Scala 元组（Tuples） 
```
DataStream<Tuple2<String, Integer>> personStream = env.fromElements( 
        new Tuple2("Adam", 17), 
        new Tuple2("Sarah", 23) ); 
personStream.filter(p -> p.f1 > 18); 
```

#### 5.4.3、Scala 样例类（case classes） 
```
case class Person(name: String, age: Int)  
val persons: DataStream[Person] = env.fromElements( 
    Person("Adam", 17),  
    Person("Sarah", 23)) 
persons.filter(p => p.age > 18) 
```

#### 5.4.4、Java 简单对象（POJOs） 
```
public class Person { 
public String name; 
public int age; 
  public Person() {} 
  public Person(String name, int age) {  
    this.name = name;       
    this.age = age;   
  } 
} 
DataStream<Person> persons = env.fromElements(    
new Person("Alex", 42),    
new Person("Wendy", 23)); 
```

#### 5.4.5、其它（Arrays, Lists, Maps, Enums, 等等） 
Flink 对 Java 和 Scala 中的一些特殊目的的类型也都是支持的，比如 Java 的ArrayList，HashMap，Enum 等等。    

### 5.5、实现UDF 函数——更细粒度的控制流 
#### 5.5.1、函数类（Function Classes） 
Flink 暴露了所有 udf 函数的接口(实现方式为接口或者抽象类)。例如MapFunction, FilterFunction, ProcessFunction 等等。    

下面例子实现了 FilterFunction 接口：   
```
DataStream<String> flinkTweets = tweets.filter(new FlinkFilter()); 
 
public static class FlinkFilter implements FilterFunction<String> { 
    @Override 
    public boolean filter(String value) throws Exception { 
        return value.contains("flink"); 
 } 
} 
```
还可以将函数实现成匿名类
```
DataStream<String> flinkTweets = tweets.filter(new FilterFunction<String>() { 
    @Override 
    public boolean filter(String value) throws Exception { 
        return value.contains("flink"); 
    } 
}); 
```
我们 filter 的字符串"flink"还可以当作参数传进去。 
```
DataStream<String> tweets = env.readTextFile("INPUT_FILE "); 
 
DataStream<String> flinkTweets = tweets.filter(new KeyWordFilter("flink")); 
 
public static class KeyWordFilter implements FilterFunction<String> { 
    private String keyWord; 
 
    KeyWordFilter(String keyWord) { this.keyWord = keyWord; } 
 
    @Override 
    public boolean filter(String value) throws Exception { 
        return value.contains(this.keyWord); 
    } 
} 
```

#### 5.5.2、匿名函数（Lambda Functions） 
```
DataStream<String> tweets = env.readTextFile("INPUT_FILE"); 
 
DataStream<String> flinkTweets = tweets.filter( tweet -> tweet.contains("flink") ); 
```

#### 5.5.3、富函数（Rich Functions） 
“富函数”是 DataStream API 提供的一个函数类的接口，所有 Flink 函数类都有其 Rich 版本。它与常规函数的不同在于，可以获取运行环境的上下文，并拥有一些生命周期方法，所以可以实现更复杂的功能。    
- RichMapFunction   
- RichFlatMapFunction   
- RichFilterFunction   

Rich Function 有一个生命周期的概念。典型的生命周期方法有：    
- open()方法是 rich function 的初始化方法，当一个算子例如 map 或者 filter被调用之前 open()会被调用。 
- close()方法是生命周期中的最后一个调用的方法，做一些清理工作。 
- getRuntimeContext()方法提供了函数的 RuntimeContext 的一些信息，例如函数执行的并行度，任务的名字，以及 state 状态 

```
public static class MyMapFunction extends RichMapFunction<SensorReading, 
Tuple2<Integer, String>> { 
    @Override 
    public Tuple2<Integer, String> map(SensorReading value) throws Exception { 
        return new Tuple2<>(getRuntimeContext().getIndexOfThisSubtask(), 
value.getId()); 
    } 
 
    @Override 
    public void open(Configuration parameters) throws Exception { 
        System.out.println("my map open"); 
        // 以下可以做一些初始化工作，例如建立一个和 HDFS的连接 
    } 
 
    @Override 
    public void close() throws Exception { 
        System.out.println("my map close"); 
        // 以下做一些清理工作，例如断开和 HDFS的连接 
    } 
} 
```

### 5.6、Sink 
Flink 没有类似于 spark 中 foreach 方法，让用户进行迭代的操作。虽有对外的输出操作都要利用 Sink 完成。最后通过类似如下方式完成整个任务最终输出操作。  
```
stream.addSink(new MySink(xxxx)) 
```
官方提供了一部分的框架的 sink。除此以外，需要用户自定义实现 sink。      
![](./images/flink-36.png)     

#### 5.6.1、Kafka 
```
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka-0.11_2.12</artifactId>
    <version>1.10.1</version>
</dependency>
```
主函数中添加 sink：    
```
dataStream.addSink(new FlinkKafkaProducer011[String]("localhost:9092", 
"test", new SimpleStringSchema())) 
```

#### 5.6.2、Redis 
```
<dependency>
    <groupId>org.apache.bahir</groupId>
    <artifactId>flink-connector-redis_2.11</artifactId>
    <version>1.0</version>
</dependency>
```
定义一个 redis 的 mapper 类，用于定义保存到 redis 时调用的命令：    
```
public static class MyRedisMapper implements RedisMapper<SensorReading>{ 
 
    // 保存到 redis的命令，存成哈希表  
    public RedisCommandDescription getCommandDescription() { 
        return new RedisCommandDescription(RedisCommand.HSET, "sensor_tempe"); 
    } 
 
    public String getKeyFromData(SensorReading data) { 
        return data.getId(); 
    } 
 
    public String getValueFromData(SensorReading data) { 
        return data.getTemperature().toString(); 
    } 
}
```
在主函数中调用：   
```
FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder() 
        .setHost("localhost") 
        .setPort(6379) 
        .build(); 
dataStream.addSink( new RedisSink<SensorReading>(config, new MyRedisMapper()) ); 
```

#### 5.6.3、Elasticsearch   
```
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-elasticsearch6_2.12</artifactId>
    <version>1.10.1</version>
</dependency>
```
在主函数中调用： 
```
// es的 httpHosts配置 
ArrayList<HttpHost> httpHosts = new ArrayList<>(); 
httpHosts.add(new HttpHost("localhost", 9200)); 
 
dataStream.addSink( new ElasticsearchSink.Builder<SensorReading>(httpHosts, new 
MyEsSinkFunction()).build()); 
```
ElasitcsearchSinkFunction 的实现：   
```
public static class MyEsSinkFunction implements 
ElasticsearchSinkFunction<SensorReading>{ 
    @Override 
    public void process(SensorReading element, RuntimeContext ctx, RequestIndexer indexer) { 
 
        HashMap<String, String> dataSource = new HashMap<>(); 
        dataSource.put("id", element.getId()); 
        dataSource.put("ts", element.getTimestamp().toString()); 
        dataSource.put("temp", element.getTemperature().toString()); 
 
        IndexRequest indexRequest = Requests.indexRequest() 
                .index("sensor") 
                .type("readingData") 
                .source(dataSource); 
 
        indexer.add(indexRequest); 
    } 
} 
```

#### 5.6.4、JDBC 自定义 sink 
```
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.44</version>
</dependency>
```
添加 MyJdbcSink 
```
public class SinkTest4_Jdbc {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读取数据
//        DataStream<String> inputStream = env.readTextFile("D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\sensor.txt");
//
//        // 转换成SensorReading类型
//        DataStream<SensorReading> dataStream = inputStream.map(line -> {
//            String[] fields = line.split(",");
//            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
//        });

        DataStream<SensorReading> dataStream = env.addSource(new SourceTest4_UDF.MySensorSource());

        dataStream.addSink(new MyJdbcSink());

        env.execute();
    }

    // 实现自定义的SinkFunction
    public static class MyJdbcSink extends RichSinkFunction<SensorReading> {
        // 声明连接和预编译语句
        Connection connection = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        @Override
        public void open(Configuration parameters) throws Exception {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123456");
            insertStmt = connection.prepareStatement("insert into sensor_temp (id, temp) values (?, ?)");
            updateStmt = connection.prepareStatement("update sensor_temp set temp = ? where id = ?");
        }

        // 每来一条数据，调用连接，执行sql
        @Override
        public void invoke(SensorReading value, Context context) throws Exception {
            // 直接执行更新语句，如果没有更新那么就插入
            updateStmt.setDouble(1, value.getTemperature());
            updateStmt.setString(2, value.getId());
            updateStmt.execute();
            if( updateStmt.getUpdateCount() == 0 ){
                insertStmt.setString(1, value.getId());
                insertStmt.setDouble(2, value.getTemperature());
                insertStmt.execute();
            }
        }

        @Override
        public void close() throws Exception {
            insertStmt.close();
            updateStmt.close();
            connection.close();
        }
    }
}
```

## 六、Flink 中的 Window 
### 6.1、Window 
#### 6.1.1、Window 概述 
streaming 流式计算是一种被设计用于处理无限数据集的数据处理引擎，而无限数据集是指一种不断增长的本质上无限的数据集，而 window 是一种切割无限数据为有限块进行处理的手段。    

Window 是无限数据流处理的核心，Window 将一个无限的 stream 拆分成有限大小的”buckets”桶，我们可以在这些桶上做计算操作。    

#### 6.1.2、Window 类型 
Window 可以分成两类：   
- CountWindow：按照指定的数据条数生成一个 Window，与时间无关。   
- TimeWindow：按照时间生成 Window。   

对于 TimeWindow，可以根据窗口实现原理的不同分成三类：滚动窗口（Tumbling Window）、滑动窗口（Sliding Window）和会话窗口（Session Window）。   

1、滚动窗口（Tumbling Windows）    
将数据依据固定的窗口长度对数据进行切片。     
特点：时间对齐，窗口长度固定，没有重叠。    
滚动窗口分配器将每个元素分配到一个指定窗口大小的窗口中，滚动窗口有一个固定的大小，并且不会出现重叠。例如：如果你指定了一个 5 分钟大小的滚动窗口，窗口的创建如下图所示：    
![](./images/flink-37.png)     
适用场景：适合做 BI 统计等（做每个时间段的聚合计算）。    

2、滑动窗口（Sliding Windows）    
滑动窗口是固定窗口的更广义的一种形式，滑动窗口由固定的窗口长度和滑动间隔组成。    
特点：时间对齐，窗口长度固定，可以有重叠。     
滑动窗口分配器将元素分配到固定长度的窗口中，与滚动窗口类似，窗口的大小由窗口大小参数来配置，另一个窗口滑动参数控制滑动窗口开始的频率。因此，滑动窗口如果滑动参数小于窗口大小的话，窗口是可以重叠的，在这种情况下元素会被分配到多个窗口中。    
例如，你有 10 分钟的窗口和 5 分钟的滑动，那么每个窗口中 5 分钟的窗口里包含着上个 10 分钟产生的数据，如下图所示：    
![](./images/flink-38.png)    
适用场景：对最近一个时间段内的统计（求某接口最近 5min 的失败率来决定是否要报警）。    

3、会话窗口（Session Windows）   
由一系列事件组合一个指定时间长度的 timeout 间隙组成，类似于 web 应用的session，也就是一段时间没有接收到新数据就会生成新的窗口。    
特点：时间无对齐。     
session 窗口分配器通过 session 活动来对元素进行分组，session 窗口跟滚动窗口和滑动窗口相比，不会有重叠和固定的开始时间和结束时间的情况，相反，当它在一个固定的时间周期内不再收到元素，即非活动间隔产生，那个这个窗口就会关闭。一个 session 窗口通过一个 session 间隔来配置，这个 session 间隔定义了非活跃周期的长度，当这个非活跃周期产生，那么当前的 session 将关闭并且后续的元素将被分配到新的 session 窗口中去。    
![](./images/flink-39.png)   

### 6.2、Window API 
#### 6.2.1、TimeWindow 
TimeWindow 是将指定时间范围内的所有数据组成一个 window，一次对一个window 里面的所有数据进行计算。    

1、滚动窗口     
Flink 默认的时间窗口根据 Processing Time 进行窗口的划分，将 Flink 获取到的数据根据进入 Flink 的时间划分到不同的窗口中。   
```
DataStream<Tuple2<String, Double>> minTempPerWindowStream = dataStream 
                .map(new MapFunction<SensorReading, Tuple2<String, Double>>() { 
                    @Override 
                    public Tuple2<String, Double> map(SensorReading value) throws Exception { 
                        return new Tuple2<>(value.getId(), value.getTemperature()); 
                    } 
                }) 
                .keyBy(data -> data.f0)  
                .timeWindow( Time.seconds(15) ) 
                .minBy(1); 
```
时间间隔可以通过 Time.milliseconds(x)，Time.seconds(x)，Time.minutes(x)等其中的一个来指定。    

2、滑动窗口（SlidingEventTimeWindows）
滑动窗口和滚动窗口的函数名是完全一致的，只是在传参数时需要传入两个参数，一个是 window_size，一个是 sliding_size。    

下面代码中的 sliding_size 设置为了 5s，也就是说，每 5s 就计算输出结果一次，每一次计算的 window 范围是 15s 内的所有元素。    
```
DataStream<SensorReading> minTempPerWindowStream = dataStream 
                .keyBy(SensorReading::getId)  
                .timeWindow( Time.seconds(15), Time.seconds(5) ) 
                .minBy("temperature"); 
```
时间间隔可以通过 Time.milliseconds(x)，Time.seconds(x)，Time.minutes(x)等其中的一个来指定。    

#### 6.2.2、CountWindow 
CountWindow 根据窗口中相同 key 元素的数量来触发执行，执行时只计算元素数量达到窗口大小的 key 对应的结果。   
注意：CountWindow 的 window_size 指的是相同 Key 的元素的个数，不是输入的所有元素的总数。    

1、滚动窗口    
默认的 CountWindow 是一个滚动窗口，只需要指定窗口大小即可，当元素数量达到窗口大小时，就会触发窗口的执行。    
```
DataStream<SensorReading> minTempPerWindowStream = dataStream 
                .keyBy(SensorReading::getId)  
                .countWindow( 5 ) 
                .minBy("temperature"); 
```

2、滑动窗口    
滑动窗口和滚动窗口的函数名是完全一致的，只是在传参数时需要传入两个参数，一个是 window_size，一个是 sliding_size。    
下面代码中的 sliding_size 设置为了 2，也就是说，每收到两个相同 key 的数据就计算一次，每一次计算的 window 范围是 10 个元素。    
```
DataStream<SensorReading> minTempPerWindowStream = dataStream 
                .keyBy(SensorReading::getId)  
                .countWindow( 10, 2 ) 
                .minBy("temperature"); 
```

#### 6.2.3、window function 
window function 定义了要对窗口中收集的数据做的计算操作，主要可以分为两类：    

增量聚合函数（incremental aggregation functions）    
每条数据到来就进行计算，保持一个简单的状态。典型的增量聚合函数有ReduceFunction, AggregateFunction。     

全窗口函数（full window functions）    
先把窗口所有数据收集起来，等到计算的时候会遍历所有数据。   
ProcessWindowFunction 就是一个全窗口函数。    

#### 6.2.4、其它可选 API 
.trigger() —— 触发器    
定义 window 什么时候关闭，触发计算并输出结果    

.evitor() —— 移除器   
定义移除某些数据的逻辑   

.allowedLateness() —— 允许处理迟到的数据   
.sideOutputLateData() —— 将迟到的数据放入侧输出流     
.getSideOutput() —— 获取侧输出流    

![](./images/flink-40.png)   

## 七、时间语义与 Wartermark 
### 7.1、Flink 中的时间语义 
在 Flink 的流式处理中，会涉及到时间的不同概念，如下图所示：    
![](./images/flink-41.png)   

Event Time：是事件创建的时间。它通常由事件中的时间戳描述，例如采集的日志数据中，每一条日志都会记录自己的生成时间，Flink 通过时间戳分配器访问事件时间戳。    
Ingestion Time：是数据进入 Flink 的时间。    
Processing Time：是每一个执行基于时间操作的算子的本地系统时间，与机器相关，默认的时间属性就是 Processing Time。    

一个例子——电影《星球大战》：    
![](./images/flink-42.png)   

例如，一条日志进入 Flink 的时间为 2017-11-12 10:00:00.123，到达 Window 的系统时间为 2017-11-12 10:00:01.234，日志的内容如下：    
```
2017-11-02 18:37:15.624 INFO Fail over to rm2 
```
对于业务来说，要统计 1min 内的故障日志个数，哪个时间是最有意义的？—— eventTime，因为我们要根据日志的生成时间进行统计。    

### 7.2、EventTime 的引入 
在 Flink 的流式处理中，绝大部分的业务都会使用 eventTime，一般只在eventTime 无法使用时，才会被迫使用 ProcessingTime 或者 IngestionTime。   
如果要使用 EventTime，那么需要引入 EventTime 的时间属性，引入方式如下所示：    
```
StreamExecutionEnvironment env = 
StreamExecutionEnvironment.getExecutionEnvironment 
// 从调用时刻开始给 env创建的每一个 stream追加时间特征
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) 
```

### 7.3、Watermark 
#### 7.3.1、基本概念 
我们知道，流处理从事件产生，到流经 source，再到 operator，中间是有一个过程和时间的，虽然大部分情况下，流到 operator 的数据都是按照事件产生的时间顺序来的，但是也不排除由于网络、分布式等原因，导致乱序的产生，所谓乱序，就是指 Flink 接收到的事件的先后顺序不是严格按照事件的 Event Time 顺序排列的。    
![](./images/flink-43.png)   

那么此时出现一个问题，一旦出现乱序，如果只根据 eventTime 决定 window 的运行，我们不能明确数据是否全部到位，但又不能无限期的等下去，此时必须要有个机制来保证一个特定的时间后，必须触发 window 去进行计算了，这个特别的机制，就是 Watermark。      
- Watermark 是一种衡量 Event Time 进展的机制。    
- Watermark 是用于处理乱序事件的，而正确的处理乱序事件，通常用Watermark 机制结合 window 来实现。 
- 数据流中的 Watermark 用于表示 timestamp 小于 Watermark 的数据，都已经到达了，因此，window 的执行也是由 Watermark 触发的。    
- Watermark 可以理解成一个延迟触发机制，我们可以设置 Watermark 的延时时长t，每次系统会校验已经到达的数据中最大的 maxEventTime，然后认定 eventTime小于 maxEventTime - t 的所有数据都已经到达，如果有窗口的停止时间等于maxEventTime – t，那么这个窗口被触发执行。    

有序流的 Watermarker 如下图所示：（Watermark 设置为 0）    
![](./images/flink-44.png)   

乱序流的 Watermarker 如下图所示：（Watermark 设置为 2）   
![](./images/flink-45.png)   

当 Flink 接收到数据时，会按照一定的规则去生成 Watermark，这条 Watermark就等于当前所有到达数据中的 maxEventTime - 延迟时长，也就是说，Watermark 是基于数据携带的时间戳生成的，一旦 Watermark 比当前未触发的窗口的停止时间要晚，那么就会触发相应窗口的执行。由于 event time 是由数据携带的，因此，如果运行过程中无法获取新的数据，那么没有被触发的窗口将永远都不被触发。    

上图中，我们设置的允许最大延迟到达时间为 2s，所以时间戳为 7s 的事件对应的 Watermark 是 5s，时间戳为 12s 的事件的 Watermark 是 10s，如果我们的窗口 1是 1s~5s，窗口 2 是 6s~10s，那么时间戳为 7s 的事件到达时的 Watermarker 恰好触发窗口 1，时间戳为 12s 的事件到达时的 Watermark 恰好触发窗口 2。    

Watermark 就是触发前一窗口的“关窗时间”，一旦触发关门那么以当前时刻为准在窗口范围内的所有所有数据都会收入窗中。     

只要没有达到水位那么不管现实中的时间推进了多久都不会触发关窗。    

#### 7.3.2、Watermark 的引入 
watermark 的引入很简单，对于乱序数据，最常见的引用方式如下：    
```
dataStream.assignTimestampsAndWatermarks( new 
    BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.millisecond s(1000)) {   
    @Override 
    public long extractTimestamp(element: SensorReading): Long = 
    {     return element.getTimestamp() * 1000L;   } } ); 
```
Event Time 的使用一定要指定数据源中的时间戳。否则程序无法知道事件的事件时间是什么(数据源里的数据没有时间戳的话，就只能使用 Processing Time 了)。    

我们看到上面的例子中创建了一个看起来有点复杂的类，这个类实现的其实就是分配时间戳的接口。Flink 暴露了 TimestampAssigner 接口供我们实现，使我们可以自定义如何从事件数据中抽取时间戳。    
```
StreamExecutionEnvironment env = 
StreamExecutionEnvironment.getExecutionEnvironment(); 
 
// 设置事件时间语义 
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); 
 
DataStream<SensorReading> dataStream = env.addSource(new SensorSource()) 
        .assignTimestampsAndWatermarks(new MyAssigner()); 
```
MyAssigner 有两种类型    
AssignerWithPeriodicWatermarks   
AssignerWithPunctuatedWatermarks   
以上两个接口都继承自 TimestampAssigner。   

**Assigner with periodic watermarks**    
周期性的生成 watermark：系统会周期性的将 watermark 插入到流中(水位线也是一种特殊的事件!)。默认周期是 200 毫秒。可以使用。  
ExecutionConfig.setAutoWatermarkInterval()方法进行设置。    
```
// 每隔 5秒产生一个 watermark 
env.getConfig.setAutoWatermarkInterval(5000); 
```
产生 watermark 的逻辑：每隔 5 秒钟，Flink 会调用
AssignerWithPeriodicWatermarks 的 getCurrentWatermark()方法。如果方法返回一个时间戳大于之前水位的时间戳，新的 watermark 会被插入到流中。这个检查保证了水位线是单调递增的。如果方法返回的时间戳小于等于之前水位的时间戳，则不会产生新的 watermark。    

例子，自定义一个周期性的时间戳抽取：    
```
// 自定义周期性时间戳分配器 
public static class MyPeriodicAssigner implements AssignerWithPeriodicWatermarks<SensorReading>{ 
     
    private Long bound = 60 * 1000L;    // 延迟一分钟    
    private Long maxTs = Long.MIN_VALUE;    // 当前最大时间戳 
 
    @Nullable 
    @Override 
    public Watermark getCurrentWatermark() { 
        return new Watermark(maxTs - bound); 
    } 
 
    @Override 
    public long extractTimestamp(SensorReading element, long previousElementTimestamp) { 
        maxTs = Math.max(maxTs, element.getTimestamp()); 
        return element.getTimestamp(); 
    } 
} 
```
一种简单的特殊情况是，如果我们事先得知数据流的时间戳是单调递增的，也就是说没有乱序，那我们可以使用 AscendingTimestampExtractor，这个类会直接使用数据的时间戳生成 watermark。    
```
DataStream<SensorReading> dataStream = … 
 
dataStream.assignTimestampsAndWatermarks( 
new AscendingTimestampExtractor<SensorReading>() { 
    @Override 
    public long extractAscendingTimestamp(SensorReading element) { 
        return element.getTimestamp() * 1000; 
    } 
});
```
而对于乱序数据流，如果我们能大致估算出数据流中的事件的最大延迟时间，就可以使用如下代码：    
```
DataStream<SensorReading> dataStream = … 
 
dataStream.assignTimestampsAndWatermarks( 
new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(1)) { 
    @Override 
    public long extractTimestamp(SensorReading element) { 
        return element.getTimestamp() * 1000L; 
    } 
});
```

**Assigner with punctuated watermarks**    
间断式地生成 watermark。和周期性生成的方式不同，这种方式不是固定时间的，而是可以根据需要对每条数据进行筛选和处理。直接上代码来举个例子，我们只给sensor_1 的传感器的数据流插入 watermark：  
```
public static class MyPunctuatedAssigner implements AssignerWithPunctuatedWatermarks<SensorReading>{ 
 
    private Long bound = 60 * 1000L;    // 延迟一分钟 
 
    @Nullable 
    @Override 
    public Watermark checkAndGetNextWatermark(SensorReading lastElement, long extractedTimestamp) { 
        if(lastElement.getId().equals("sensor_1")) 
            return new Watermark(extractedTimestamp - bound); 
        else 
            return null; 
    } 
 
    @Override 
    public long extractTimestamp(SensorReading element, long previousElementTimestamp) { 
        return element.getTimestamp(); 
    } 
}
```

### 7.4、EvnetTime 在window 中的使用（Scala 版） 
#### 7.4.1、滚动窗口（TumblingEventTimeWindows）  
```
def main(args: Array[String]): Unit = { 
    //  环境
 
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment 
 
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) 
    env.setParallelism(1) 
 
    val dstream: DataStream[String] = env.socketTextStream("localhost",7777) 
 
    val textWithTsDstream: DataStream[(String, Long, Int)] = dstream.map 
    { text => 
          val arr: Array[String] = text.split(" ") 
          (arr(0), arr(1).toLong, 1) 
        } 
    val textWithEventTimeDstream: DataStream[(String, Long, Int)] = 
    textWithTsDstream.assignTimestampsAndWatermarks(new 
    BoundedOutOfOrdernessTimestampExtractor[(String, Long, 
    Int)](Time.milliseconds(1000)) { 
      override def extractTimestamp(element: (String, Long, Int)): Long = { 
 
       return  element._2 
      } 
    }) 
 
    val textKeyStream: KeyedStream[(String, Long, Int), Tuple] = 
    textWithEventTimeDstream.keyBy(0) 
    textKeyStream.print("textkey:") 
 
    val windowStream: WindowedStream[(String, Long, Int), Tuple, TimeWindow] = textKeyStream.window(TumblingEventTimeWindows.of(Time.seconds(2))) 
 
    val groupDstream: DataStream[mutable.HashSet[Long]] = windowStream.fold(new mutable.HashSet[Long]()) { case (set, (key, ts, count)) => 
      set += ts 
    } 
 
    groupDstream.print("window::::").setParallelism(1) 
 
    env.execute() 
  } 
} 
```
结果是按照 Event Time 的时间窗口计算得出的，而无关系统的时间（包括输入的快慢）。    

#### 7.4.2、滑动窗口（SlidingEventTimeWindows） 

#### 7.4.3、会话窗口（EventTimeSessionWindows） 












