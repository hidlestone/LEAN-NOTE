# gmt_create&gmt_modified如何维护

```sql
`gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
```
阿里java开发手册：
```
【强制】表必备三字段：id, gmt_create, gmt_modified。 
说明：其中 id 必为主键，类型为 bigint unsigned、单表时自增、步长为 1。
gmt_create, gmt_modified 的类型均为 datetime 类型，
前者现在时表示主动创建，后者过去分词表示被动更新。 
```

这两个字段如何维护：   
MySQL中DATETIME、DATE和TIMESTAMP类型的区别

DATETIME
```
显示格式：YYYY-MM-DD HH:MM:SS
时间范围:[ '1000-01-01 00:00:00'到'9999-12-31 23:59:59']
```

DATE
```
显示格式：YYYY-MM-DD
时间范围：['1000-01-01'到'9999-12-31']
```

TIMESTAMP
```
显示格式：YYYY-MM-DD HH:MM:SS
时间范围:[ '1970-01-01 00:00:00'到'2037-12-31 23:59:59']
```

TIMESTAMP注意点
```
TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  
    在创建新记录和修改现有记录的时候都对这个数据列刷新。
TIMESTAMP DEFAULT CURRENT_TIMESTAMP  
    在创建新记录的时候把这个字段设置为当前时间，但以后修改时，不再刷新它。
TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  
    在创建新记录的时候把这个字段设置为0，以后修改时刷新它。
TIMESTAMP DEFAULT ‘yyyy-mm-dd hh:mm:ss’ ON UPDATE CURRENT_TIMESTAMP  
    在创建新记录的时候把这个字段设置为给定值，以后修改时刷新它
```

其他注意点：
```
1、TIMESTAMP列不为空时，默认值可以为“0000-00-00 00:00:00”，但不能为null。
2、一个表可以存在多个TIMESTAMP列，但一个表只有一个TIMESTAMP类型的字段可以在默认值或者UPDATE部分用CURRENT_TIMESTAMP，即设置为数据更新而改变为数据库系统当前值。
    --> 在MySQL 【5.6.5】版本之前，是只能有一列能够在初始化的时候或者被更新的时候自动设置为CURRENT_TIMESTAMP的值。
        这个限制在后续版本中取消了，后续版本的MySQL支持任意列被设置为DEFAULT CURRENT_TIMESTAMP以及 ON UPDATE CURRENT_TIMESTAMP时自动更新值。
        MySQL的release Note中也有说明。https://dev.mysql.com/doc/relnotes/mysql/5.6/en/news-5-6-5.html。
3、TIMESTAMP列的默认值是CURRENT_TIMESTAMP常量值。当记录数据发生变化的时候，TIMESTAMP列会自动将其值设定为CURRENT_TIMESTAMP。
4、TIMESTAMP列创建后的格式是：
ALTER TABLE `course`
ADD COLUMN `birthday`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
 
ALTER TABLE `course`
ADD COLUMN `birthday`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ;
 
ALTER TABLE `course`
ADD COLUMN `birthday`  timestamp NULL AFTER `cname`;
```


DATETIME和TIMESTAMP的一些区别和问题
```
a）DATETIME的默认值为null；TIMESTAMP的字段默认不为空（not null）,默认值为当前时间（CURRENT_TIMESTAMP），如果不做特殊处理，并且update语句中没有指定该列的更新值，则默认更新为当前时间。
这个区别就解释了为什么平时我们都不用可以管这个字段就能自动更新了，因为多数时候用的是timestamp；而此处用的是datetime，不会有自动更新当前时间的机制，所以需要在上层手动更新该字段

b）DATETIME使用8字节的存储空间，TIMESTAMP的存储空间为4字节。因此，TIMESTAMP比DATETIME的空间利用率更高。
这个区别解释了为啥timestamp类型用的多

c）两者的存储方式不一样 ，对于TIMESTAMP，它把客户端插入的时间从当前时区转化为UTC（世界标准时间）进行存储。查询时，将其又转化为客户端当前时区进行返回。
而对于DATETIME，不做任何改变，基本上是原样输入和输出。

d）两者所能存储的时间范围不一样?
timestamp所能存储的时间范围为：’1970-01-01 00:00:01.000000’ 到 ‘2038-01-19 03:14:07.999999’；
datetime所能存储的时间范围为：’1000-01-01 00:00:00.000000’ 到 ‘9999-12-31 23:59:59.999999’。
```

CURRENT_TIMESTAMP为什么能用于datetime类型 。 select version()。
```
在mysql 5.6之前的版本，CURRENT_TIMESTAMP只能用于timestamp类型，
【5.6】版本之后，CURRENT_TIMESTAMP也能用于datetime类型了。
select version() ; 可查询数据库版本。
```

如何选用：      
DATETIME和时区无关，TIMESTAMP与时区有关。更改了数据库的时区，取出的数据会发生改变。
这两个字段做如下设置，前提是 mysql的版本在 5.6 以上。则数据库会自己维护这两个字段。

```sql
`gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
```

