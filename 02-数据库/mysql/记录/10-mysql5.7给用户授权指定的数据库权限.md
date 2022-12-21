# 10-mysql5.7给用户授权指定的数据库权限

## 1、使用 root 管理员登陆 mysql
```sql
mysql -uroot -p;
```

## 2、创建新用户
```sql
CREATE USER 'CPSAdmin'@'%' IDENTIFIED BY 'CPSAdmin123'; 
```
'%' - 所有情况都能访问  
‘localhost’ - 本机才能访问  
’111.222.33.44‘ - 指定 ip 才能访问  

## 3、给该用户添加权限
```sql
grant all privileges on gps_data.* to 'CPSAdmin'@'%';
grant all privileges on gps_hisData.* to 'CPSAdmin'@'%';
```
all 可以替换为 select,delete,update,create,drop

## 4、删除用户
```sql
Delete FROM mysql.user Where User='user1';
```

## 5、可能遇到的问题
```sql
flush privileges;
```

