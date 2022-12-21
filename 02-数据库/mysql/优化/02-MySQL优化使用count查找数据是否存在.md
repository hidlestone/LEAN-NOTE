# 02-MySQL优化使用count查找数据是否存在

在根据某条件查询数据库中的数据 ''有'' 或 ''没有'' 两种状态时,大多写法为:
```sql
SELECT count(*) FROM table WHERE x = 1 AND y = 2
```

在应用中判断返回值是否大于0。

优化方案:
```sql
SELECT 1 FROM table WHERE x = 1 AND y = 2 LIMIT 1
```

在应用中对返回值判空。  
这样在扫描到一条是就会返回不再继续查找，数据量越大优化余越明显。   




