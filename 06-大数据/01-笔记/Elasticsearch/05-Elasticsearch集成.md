# 05-Elasticsearch集成
## 一、SpringData-整体介绍
Spring Data是一个用于简化数据库、非关系型数据库、索引库访问，并支持云服务的开源框架。其主要目标是使得对数据的访问变得方便快捷，并支持 map-reduce框架和云计算数据服务。Spring Data可以极大的简化JPA(Elasticsearch…)的写法，可以在几乎不用写实现的情况下，实现对数据的访问和操作。除了CRUD 外，还包括如分页、排序等一些常用的功能。

### 1.1、Spring Data 常用的功能模块如下：
- Spring Data JDBC
- Spring Data JPA
- Spring Data LDAP
- Spring Data MongoDB
- Spring Data Redis
- Spring Data R2DBC
- Spring Data REST
- Spring Data for Apache Cassandra
- Spring Data for Apache Geode
- Spring Data for Apache Solr
- Spring Data for Pivotal GemFire
- Spring Data Couchbase
- Spring Data Elasticsearch
- Spring Data Envers
- Spring Data Neo4j
- Spring Data JDBC Extensions
- Spring for Apache Hadoop

### 1.2、Spring Data Elasticsearch 介绍
Spring Data Elasticsearch基于Spring Data API简化 Elasticsearch 操作，将原始操作Elasticsearch 的客户端API进行封装。Spring Data为Elasticsearch 项目提供集成搜索引擎。Spring Data Elasticsearch POJO的关键功能区域为中心的模型与Elastichsearch交互文档和轻松地编写一个存储索引库数据访问层。

Spring Data Elasticsearch 官网：
https://spring.io/projects/spring-data-elasticsearch


















