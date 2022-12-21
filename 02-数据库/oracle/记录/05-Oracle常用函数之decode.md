# 05-Oracle常用函数之decode

Oracle数据库中如果想要在sql查询中实现if else这样的效果，不仅可以用case when语法，本身提供了一个更为简便的函数，那就是decode，下面来具体看下其使用方法。

case when
```sql
select 
case username
when 'ouxio' then '12345'
when 'hahaa' then '22344'
else  'emmm'
end
as password
from users;
```
而用decode就简便不少，如下：
```sql
select 
decode (username,'ouxio','12345','hahaa','22344','emmm') as password
from users;
```
decode函数第一个参数是需要判断的字段，剩下的每两个参数代表判断值与输出值，比如ouxio和12345这两个参数第一个是指当username等于ouxio时输出12345，后面的每两个参数以此类推，这样的两两参数可写多个。而最后的一个参数emmm，意思是当需要判断的字段都没有匹配的值时输出最后一个参数值emmm，这个参数可写可不写。

decode函数和case when很相似，都能完成同样的效果，那么有什么区别呢
- 1、特有性，decode函数是Oracle特有的函数，而case when不仅Oracle，在mysql和sql server都可使用。
- 2、局限性，decode只能判断字段相等，但是可以配合sign函数进行大于、小于、等于，case when可用于更多的条件比较式，并且使用case搜索函数时可对比多个字段。
- 3、简便性，decode使用起来相对比较方便，case when虽然繁琐但是更为强大灵活。

sign()函数根据某个值是0、正数还是负数，分别返回0、1、-1，所以要用decode判断与某个数的大小可如下：
```sql
select 
select decode(sign(5-3), 1 ,'大于', -1, '少于', 0 ,'相等') from dual  
from users;
```

扩展 mysql if 函数   
mysql也有类似decode功能的函数，那就是if函数，不过功能要逊色许多，只能判断单个条件，若想判断多条件还得用case when。具体的if使用方法如下：
```sql
select if(username = 'ouxio','me','you') as iswho from users;
```
当字段username为ouxio时，输出me，不是时输出you。
