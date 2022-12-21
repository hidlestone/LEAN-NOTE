# 06-dblink

![](./images/dblink-01.png)    
![](./images/dblink-02.png)    

当用户要跨本地数据库，访问另外一个数据库表中的数据时，本地数据库中必须创建了远程数据库的dblink,通过dblink本地数据库可以像访问本地数据库一样访问远程数据库表中的数据。下面讲介绍我之前项目中如何在本地数据库中创建dblink.
```sql
create database link TestDblink
 connect to dbName identified by dbPassword
  using '(DESCRIPTION =(ADDRESS_LIST =(ADDRESS =(PROTOCOL = TCP)(HOST = 192.168.2.158)(PORT = 1521)))(CONNECT_DATA =(SERVICE_NAME = orcl)))';
  
TestDblink    表示dblink名字
dbName        表示 远程数据库的用户
dbPassword    表示 远程数据库的密码
HOST          表示远程数据库IP
PORT          表示远程数据库端口
SERVICE_NAME  远程数据库的实例名  
```

查询、删除和插入数据和操作本地的数据库是一样的，只不过表名需要写成“表名@dblink服务器”而已。     
例如：如果想在本地数据库中通过dblink访问远程数据库'orcl'中dbName.tb_test表,sql语句如下所示。   
```sql
select * from db.tb_test@TestDblink;
```

DBLINK其他相关的知识：    
1、查看所有的数据库链接，登录管理员查看  
```sql
select owner,object_name from dba_objects where object_type='DATABASE LINK';
```   
2、删除数据库连接
```sql
drop database link TestDblink;
```


查看dblink
```sql
select owner,object_name from dba_objects where object_type='DATABASE LINK';
```
或者
```sql
select * from dba_db_links;
```
删除。注意:用户有create public database link 或者create database link 权限。  
```sql
drop public database link dblinkname;
```

参考资料：   
https://blog.csdn.net/qq_30553235/article/details/78688352
