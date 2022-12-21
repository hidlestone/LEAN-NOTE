# 13-mysql编码

mysql中的utf8mb4、utf8mb4_unicode_ci、utf8mb4_general_ci

## 1、utf8与utf8mb4（utf8 most bytes 4）
```
MySQL 5.5.3之后增加了utfmb4字符编码
支持BMP（Basic Multilingual Plane，基本多文种平面）和补充字符
最多使用四个字节存储字符
```
utf8mb4是utf8的超集并完全兼容utf8，能够用四个字节存储更多的字符。   
标准的UTF-8字符集编码是可以使用1-4个字节去编码21位字符，这几乎包含了世界上所有能看见的语言。   
MySQL里面实现的utf8最长使用3个字符，包含了大多数字符但并不是所有。例如emoji和一些不常用的汉字，如“墅”，这些需要四个字节才能编码的就不支持。   

## 2、字符集、连接字符集、排序字符集
utf8mb4对应的排序字符集有utf8mb4_unicode_ci、utf8mb4_general_ci.   
utf8mb4_unicode_ci和utf8mb4_general_ci的对比：   
```
准确性：
utf8mb4_unicode_ci是基于标准的Unicode来排序和比较，能够在各种语言之间精确排序
utf8mb4_general_ci没有实现Unicode排序规则，在遇到某些特殊语言或者字符集，排序结果可能不一致。
但是，在绝大多数情况下，这些特殊字符的顺序并不需要那么精确。

性能
utf8mb4_general_ci在比较和排序的时候更快
utf8mb4_unicode_ci在特殊情况下，Unicode排序规则为了能够处理特殊字符的情况，实现了略微复杂的排序算法。
但是在绝大多数情况下发，不会发生此类复杂比较。相比选择哪一种collation，使用者更应该关心字符集与排序规则在db里需要统一。
```

## 3、utf8_bin和utf8_general_ci编码的区别
MySQL中存在多种格式的utf8编码，其中最常见的两种为：   
utf8_bin  
utf8_general_ci  

utf8_bin将字符串中的每一个字符用二进制数据存储，区分大小写;   
utf8_genera_ci不区分大小写，ci为case insensitive的缩写，即大小写不敏感，为utf8默认编码。    
```sql
CREATE TABLE `t_bin` (
`id` int(11) DEFAULT NULL,
`name` varchar(20) DEFAULT NULL,
UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin；

CREATE TABLE `t_ci` (
`id` int(11) DEFAULT NULL,
`name` varchar(20) DEFAULT NULL,
UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE `t_default` (
`id` int(11) DEFAULT NULL,
`name` varchar(20) DEFAULT NULL,
UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

```
root@zow 11:13:44>insert into t_bin values (1, 'Alex');
Query OK, 1 row affected (0.01 sec)

root@zow 11:14:14>insert into t_bin values (2, 'alex');
Query OK, 1 row affected (0.01 sec)

root@zow 11:14:17>insert into t_ci values (1, 'Alex');
Query OK, 1 row affected (0.00 sec)

root@zow 11:14:32>insert into t_ci values (2, 'alex');
ERROR 1062 (23000): Duplicate entry 'alex' for key 'uk_name'
root@zow 11:14:36>insert into t_default values (1, 'Alex');
Query OK, 1 row affected (0.01 sec)

root@zow 11:14:50>insert into t_default values (2, 'alex');
ERROR 1062 (23000): Duplicate entry 'alex' for key 'uk_name'
```

## 4、分析   
编码为utf8_bin时，Alex和alex被认为是两个不同的值，区分大小写；   
编码为utf8_general_ci时，即默认的编码时，Alex和alex被认为是相同的值，不区分大小写。   
```sql
ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='退库单';
```
collate utf8_bin是 以二进制值比较，也就是区分大小写，collate是核对的意思

