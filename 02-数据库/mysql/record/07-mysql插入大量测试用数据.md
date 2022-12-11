# mysql插入大量测试用数据

### 使用 mybatis + java-faker 插入测试数据：fake-data-generator
```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>1.3.2</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.4</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-joda</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.module</groupId>
        <artifactId>jackson-module-parameter-names</artifactId>
    </dependency>
    <!-- 分页插件 -->
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
        <version>1.2.5</version>
    </dependency>
    <!-- alibaba的druid数据库连接池 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.9</version>
    </dependency>

    <dependency>
        <groupId>com.github.javafaker</groupId>
        <artifactId>javafaker</artifactId>
        <version>1.0.2</version>
    </dependency>
</dependencies>
```
执行：com.payn.fake.ApplicationTest01


### 二、利用存储过程插入测试数据
创建员工表和部门表：
```sql
create table dept(
id int unsigned primary key auto_increment,
deptno mediumint unsigned not null default 0,
dname varchar(20) not null default "",
loc varchar(13) not null default ""
)ENGINE=INNODB DEFAULT CHARSET=utf8;

create table emp(
id int unsigned primary key auto_increment,
empno mediumint unsigned not null default 0,
ename varchar(20) not null default "",
job varchar(9) not null default "",
mgr mediumint	unsigned not null default 0,
hiredate date not null,
sal decimal(7,2) not null,
comm decimal(7,2) not null,
deptno mediumint unsigned not null default 0
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```

开启二进制日志:
```sql
show variables like 'log_bin_trust_function_creators'
```
可以看到默认的二进制日志时关闭的
```
Variable_name                   |Value |
--------------------------------|------|
log_bin_trust_function_creators |OFF   |
```
通过下面的命令进行设置:
```sql
set global log_bin_trust_function_creators=1
```
可以看到二进制日志变成开启
```
Variable_name                   |Value |
--------------------------------|------|
log_bin_trust_function_creators |ON    |
```

创建一个生成随机字符串的函数:
```
DELIMITER $$
create FUNCTION rand_string(n int) returns varchar(255)
BEGIN
	declare chars_str varchar(100) default 'abcdefghijklmnoprstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
	declare return_str varchar(255) 	default '';
	declare i int default 0;
	while i<n DO
	set return_str=CONCAT(return_str,substring(chars_str,floor(1+rand()*52),1));
	set i=i+1;
	end while;
	return return_str;
END $$
```
通过DELIMITER定义mysql语句的结束符,因为函数中多处有分号,如果不修改掉默认的结束符;那么函数将会产生错误。

随后,创建生成随机数的函数:
```
delimiter $$
create function rand_num() RETURNS int(5)
begin 
	declare i int default 0;
	set i=floor(100+rand()*10);
	return i;
end $$
```

插入员工表函数:
```
delimiter $$
create procedure insert_emp(IN START INT(10),IN max_num int(10))
begin 
	declare i int default 0;
	set autocommit=0;
	repeat
	set i=i+1;
	insert into emp(empno,ename,job,mgr,hiredate,sal,comm,deptno) values((start+i),rand_string(6),'SALESMAN',0001,CURDATE(),2000,400,rand_num());
	until i=max_num
	end repeat;
	commit;
end $$
```

插入部门的函数:
```
delimiter $$
create procedure insert_dept(IN START INT(10),IN max_num INT(10))
begin
	declare i int default 0;
	set autocommit=0;
	repeat
	set i=i+1;
	insert into dept(deptno,dname,loc) values((start+i),rand_string(10),rand_string(8));
	until i=max_num
	end repeat;
	commit;
	end $$
end $$
```

调用上边的两个插入函数
```
call insert_dept(100,10);
```
在员工表中插入50W条数据, 
```
call insert_emp(100001,500000);
```


