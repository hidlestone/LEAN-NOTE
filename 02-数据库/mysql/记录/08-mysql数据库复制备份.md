# mysql数据库复制备份

#### 一、将旧表复制到新表
该语句只是复制表结构以及数据，它不会复制与表关联的其他数据库对象，如索引，主键约束，外键约束，触发器等。
```sql
CREATE 新表 SELECT * FROM 旧表;

CREATE TABLE if not exists new_table
SELECT col1, col2, col3
FROM existing_table
WHERE conditions;
```

MySQL 数据库不支持 SELECT ... INTO 语句，但支持 INSERT INTO ... SELECT      
只复制希望的列插入到另一个已存在的表中：
```sql
INSERT INTO  新表  (column_name(s))  SELECT   column_name(s)  FROM  旧表;
INSERT INTO Websites (name, country) SELECT app_name, country FROM apps;
```

#### 二、复制相同表结构
对于mysql的复制相同表结构方法，有create table as 和create table like 两种
```sql
create table 新表 like 旧表;
create table 新表 as select * from 旧表 limit 0;
```
两者的区别：
- as 用来创建相同表结构并复制源表数据
- like 用来创建完整表结构和全部索引
- oracle 支持 as ，也是只有表结构没有索引
- oracle 不支持 like。


#### 三、mysql 复制表到另一个数据库
访问不同数据库中的表：数据库名.表名 ，采用点的形式。有时，要将表复制到其他数据库。在这种情况下，可使用以下语句：
```sql
CREATE TABLE destination_db.new_table
LIKE source_db.existing_table;

INSERT destination_db.new_table
SELECT *FROM source_db.existing_table;
```

如下：
```sql
CREATE DATABASE IF NOT EXISTS testdb;

CREATE TABLE testdb.offices LIKE yiibaidb.offices;

INSERT testdb.offices
SELECT * FROM yiibaidb.offices;
```

#### 四、mysql 数据库导入导出
1、使用mysqldump工具将数据库导出并转储到sql文件：mysqldump -u 用户名 -p 数据库名 > 导出的文件名 
```sql
mysqldump -h IP -u 用户名 -p 数据库名 > 导出的文件名
```
2 、(导出某张表的表结构不含数据)
```
mysqldump -h localhost -u root -p -d test pollution > G:\arcgisworkspace\zypdoc\test.sql
```
3、(导出某张表的表结构和数据，不加-d)
```
mysqldump -h 127.0.0.1 -u root -p test pollution > G:\arcgisworkspace\zypdoc\test.sql
mysqldump -u root -p yiibaidb >d:\database_bak\yiibaidb.sql
```
其中>表示导出。

导入sql文件。在MySQL中新建数据库，这时是空数据库，如新建一个名为news的目标数据库。
```sql
create database if not exists news;
use news;
```
导入文件：source 路径+导入的文件名; 
```
source d:\mysql.sql;
```


### MySQL 工具之mysqldumper介绍

https://www.modb.pro/db/31576

