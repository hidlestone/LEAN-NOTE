# mysql实现序列（Sequence）效果

一般使用序列(Sequence)来处理主键字段，在MySQL中是没有序列的，但是MySQL有提供了自增长(increment)来实现类似的目的，但也只是自增，而不能设置步长、开始索引、是否循环等，最重要的是一张表只能由一个字段使用自增，但有的时候我们需要两个或两个以上的字段实现自增（单表多字段自增），MySQL本身是实现不了的，但我们可以用创建一个序列表，使用函数来获取序列的值。

1. 新建序列表
```sql
drop table if exists sequence;   
create table sequence (       
seq_name        VARCHAR(50) NOT NULL, -- 序列名称       
current_val     INT         NOT NULL, -- 当前值       
increment_val   INT         NOT NULL    DEFAULT 1, -- 步长(跨度)       
PRIMARY KEY (seq_name)   );
```

2. 新增一个序列
```sql
INSERT INTO sequence VALUES ('seq_test1_num1', '0', '1');
INSERT INTO sequence VALUES ('seq_test1_num2', '0', '2');
```

3. 创建 函数 用于获取序列当前值(v_seq_name 参数值 代表序列名称)
```sql
create function currval(v_seq_name VARCHAR(50))   
returns integer 
begin     
    declare value integer;       
    set value = 0;       
    select current_val into value  from sequence where seq_name = v_seq_name; 
   return value; 
end;
```

4. 查询当前值
```sql
select currval('seq_test1_num1');
```

5. 创建 函数 用于获取序列下一个值(v_seq_name 参数值 代表序列名称)
```sql
create function nextval (v_seq_name VARCHAR(50))
    returns integer
begin
    update sequence set current_val = current_val + increment_val  where seq_name = v_seq_name;
    return currval(v_seq_name);
end;
```

6. 查询下一个值
```sql
select nextval('seq_test1_num1');
```

7. 新建表 用于测试的表
```sql
DROP TABLE IF EXISTS `test1`;
CREATE TABLE `test1` (
  `name` varchar(255) NOT NULL,
  `value` double(255,0) DEFAULT NULL,
  `num1` int(11) DEFAULT NULL,
  `num2` int(11) DEFAULT NULL,
  PRIMARY KEY (`name`)
);
```

8. 新建触发器 插入新纪录前给自增字段赋值实现字段自增效果
```sql
CREATE TRIGGER `TRI_test1_num1` BEFORE INSERT ON `test1` FOR EACH ROW BEGIN
set NEW.num1 = nextval('seq_test1_num1');
set NEW.num2 = nextval('seq_test1_num2');
END
```

9. 最后测试自增效果
```sql
INSERT INTO test1 (name, value) VALUES ('1', '111');
INSERT INTO test1 (name, value) VALUES ('2', '222');
INSERT INTO test1 (name, value) VALUES ('3', '333');
INSERT INTO test1 (name, value) VALUES ('4', '444');
```

10. 结果展示
```sql
SELECT * FROM test1;
```

