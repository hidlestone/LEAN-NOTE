# 04-DML（Data Manipulation Language）数据操作
## 一、Load
 Load语句可将文件导入到Hive表中。

1）语法
```
hive> 
LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)];
```
关键字说明：  
（1）local：表示从本地加载数据到Hive表；否则从HDFS加载数据到Hive表。  
（2）overwrite：表示覆盖表中已有数据，否则表示追加。  
（3）partition：表示上传到指定分区，若目标是分区表，需指定分区。  

2）实操案例  
（0）创建一张表  
```
hive (default)> 
create table student(
    id int, 
    name string
) 
row format delimited fields terminated by '\t';
```
（1）加载本地文件到hive
```
hive (default)> load data local inpath '/opt/module/datas/student.txt' into table student;
```
（2）加载HDFS文件到hive中  
1、上传文件到HDFS  
```
[atguigu@hadoop102 ~]$ hadoop fs -put /opt/module/datas/student.txt /user/atguigu
```
2、加载HDFS上数据，导入完成后去HDFS上查看文件是否还存在
```
hive (default)> 
load data inpath '/user/atguigu/student.txt' 
into table student;
```
（3）加载数据覆盖表中已有的数据
1、上传文件到HDFS
```
hive (default)> dfs -put /opt/module/datas/student.txt /user/atguigu;
```
2、加载数据覆盖表中已有的数据
```
hive (default)> 
load data inpath '/user/atguigu/student.txt' 
overwrite into table student;
```

## 二、Insert
### 2.1、将查询结果插入表中
1）语法
```
INSERT (INTO | OVERWRITE) TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement;
```
关键字说明：  
（1）INTO：将结果追加到目标表  
（2）OVERWRITE：用结果覆盖原有数据  

2）案例  
（1）新建一张表  
```
hive (default)> 
create table student1(
    id int, 
    name string
) 
row format delimited fields terminated by '\t';
```
（2）根据查询结果插入数据
```
hive (default)> insert overwrite table student3 
select 
    id, 
    name 
from student;
```

### 2.2、将给定Values插入表中
1）语法
```
INSERT (INTO | OVERWRITE) TABLE tablename [PARTITION (partcol1[=val1], partcol2[=val2] ...)] VALUES values_row [, values_row ...]
```
2）案例
```
hive (default)> insert into table  student1 values(1,'wangwu'),(2,'zhaoliu');
```

### 2.3、将查询结果写入目标路径
1）语法
```
INSERT OVERWRITE [LOCAL] DIRECTORY directory
  [ROW FORMAT row_format] [STORED AS file_format] select_statement;
```
2）案例
```
insert overwrite local directory '/opt/module/datas/student' ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.JsonSerDe'
select id,name from student;
```

## 三、Export&Import
Export导出语句可将表的数据和元数据信息一并到处的HDFS路径，Import可将Export导出的内容导入Hive，表的数据和元数据信息都会恢复。Export和Import可用于两个Hive实例之间的数据迁移。   

1）语法
```
--导出
EXPORT TABLE tablename TO 'export_target_path'
--导入
IMPORT [EXTERNAL] TABLE new_or_original_tablename FROM 'source_path' [LOCATION 'import_target_path']
```
2）案例
```
--导出
hive>
export table default.student to '/user/hive/warehouse/export/student';

--导入
hive>
import table student2 from '/user/hive/warehouse/export/student';
```



