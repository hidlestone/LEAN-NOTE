# 11-常用sql

查询所有表：
```sql
select table_name from information_schema.tables where table_schema='srment6' and table_type='base table';
```

查询指定数据库中指定表的所有字段名column_name：
```sql
select column_name from information_schema.columns where table_schema='srment6' and table_name='users'
```

```sql
select * from information_schema.columns where table_schema='wms_wm'
```

https://blog.csdn.net/zhazhagu/article/details/79457354
