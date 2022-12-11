# oracle递归查询(树形查询)

#### 1、准备测试表和测试数据
```sql
create table tb_menu(
    id number(10) not null,--主键ID
    pid number(10) not null,--父菜单ID
    title varchar2(50),--菜单名称
);
```

添加数据
```sql
--父菜单
insert into tb_menu(id, pid,title ) values(1,0,'父菜单1');
insert into tb_menu(id, pid,title ) values(2,0,'父菜单2');
insert into tb_menu(id, pid,title ) values(3,0,'父菜单3');
insert into tb_menu(id, pid,title ) values(4,0,'父菜单4');
insert into tb_menu(id, pid,title ) values(5,0,'父菜单5');
--一级菜单
insert into tb_menu(id, pid,title) values(6,1,'一级菜单6');
insert into tb_menu(id, pid,title) values(7,1,'一级菜单7');
insert into tb_menu(id, pid,title) values(8,1,'一级菜单8');
insert into tb_menu(id, pid,title) values(9,2,'一级菜单9');
insert into tb_menu(id, pid,title) values(10, 2, '一级菜单10');
insert into tb_menu(id, pid,title) values(11, 2, '一级菜单11');
insert into tb_menu(id, pid,title) values(12, 3,'一级菜单12');
insert into tb_menu(id, pid,title) values(13, 3,'一级菜单13');
insert into tb_menu(id, pid,title) values(14, 3,'一级菜单14');
insert into tb_menu(id, pid,title) values(15, 4,'一级菜单15');
insert into tb_menu(id, pid,title) values(16, 4,'一级菜单16');
insert into tb_menu(id, pid,title) values(17, 4,'一级菜单17');
insert into tb_menu(id, pid,title) values(18, 5,'一级菜单18');
insert into tb_menu(id, pid,title) values(19, 5,'一级菜单19');
insert into tb_menu(id, pid,title) values(20, 5,'一级菜单20');
--二级菜单
insert into tb_menu(id, title, pid) values(21, '二级菜单21',6);
insert into tb_menu(id, title, pid) values(22, '二级菜单22',6);
insert into tb_menu(id, title, pid) values(23, '二级菜单23',7);
insert into tb_menu(id, title, pid) values(24, '二级菜单24',7);
insert into tb_menu(id, title, pid) values(25, '二级菜单25',8);
insert into tb_menu(id, title, pid) values(26, '二级菜单26',9);
insert into tb_menu(id, title, pid) values(27, '二级菜单27',10);
insert into tb_menu(id, title, pid) values(28, '二级菜单28',11);
insert into tb_menu(id, title, pid) values(29, '二级菜单29',12);
insert into tb_menu(id, title, pid) values(30, '二级菜单30',13);
insert into tb_menu(id, title, pid) values(31, '二级菜单31',14);
insert into tb_menu(id, title, pid) values(32, '二级菜单32',15);
insert into tb_menu(id, title, pid) values(33, '二级菜单33',16);
insert into tb_menu(id, title, pid) values(34, '二级菜单34',17);
insert into tb_menu(id, title, pid) values(35, '二级菜单35',18);
insert into tb_menu(id, title, pid) values(36, '二级菜单36',19);
insert into tb_menu(id, title, pid) values(37, '二级菜单37',20);
--三级菜单
insert into tb_menu(id, title, pid) values(38, '三级菜单38',21);
insert into tb_menu(id, title, pid) values(39, '三级菜单39',22);
insert into tb_menu(id, title, pid) values(40, '三级菜单40',23);
insert into tb_menu(id, title, pid) values(41, '三级菜单41',24);
insert into tb_menu(id, title, pid) values(42, '三级菜单42',25);
insert into tb_menu(id, title, pid) values(43, '三级菜单43',26);
insert into tb_menu(id, title, pid) values(44, '三级菜单44',27);
insert into tb_menu(id, title, pid) values(45, '三级菜单45',28);
insert into tb_menu(id, title, pid) values(46, '三级菜单46',28);
insert into tb_menu(id, title, pid) values(47, '三级菜单47',29);
insert into tb_menu(id, title, pid) values(48, '三级菜单48',30);
insert into tb_menu(id, title, pid) values(49, '三级菜单49',31);
insert into tb_menu(id, title, pid) values(50, '三级菜单50',31);
commit;
```
说明：pid字段存储的是当前菜单节点的上级id，如果菜单节点是顶级节点，该菜单没有上级菜单，则pid应为null，然而在表中记录最好不要为null，建议使用0代替，因为null记录会引起全文扫描。

#### 2、语法说明
oracle中递归查询（树形查询）主要依托于：
```
select * from table_name [where 条件1] start with 条件2 connect by 条件3;
```

其中，[where 条件1] 可以不需要，[where 条件1]是对根据[start with 条件2 connect by 条件3]选择出来的记录进行过滤，是针对单条记录的过滤，不会考虑树形查询的树结构；

[start with 条件2 ]限定作为搜索起始点的条件，表示从满足什么条件的记录开始查询；[connect by 条件3]表示查询的连接条件，树形菜单的查询常常是因为记录与记录之间存在某种关系，这种关系通常就作为连接条件。

补充：connect by子句，通常跟关键字“PRIOR”一起使用，“PRIOR”关键字的位置通常有两种，”CONNECT BY PRIOR ID = PID”和”CONNECT BY ID = PRIOR PID”，关键字位置的不同，相应的会导致查询结果的不同。


#### 3、递归查询（树形查询）实践
1）查询树中的所有顶级父节点（家族中辈分最大的那一代人）。假如这个树是个目录结构，那么第一个操作总是找出所有的顶级节点，然后再逐个根据顶级节点找到其子节点。
```
select * from tb_menu m where m.pid = 0;
```

以上查询得到的就是树的所有顶级节点，也就是家族中辈分最大的那一代人。         
2）查找一个节点的直属子节点（家族中某个长辈的所有儿子），此时查找的是直属的子类节点，也是用不到树形查询的。
```
select * from tb_menu m where m.pid=1;
```

3）查找一个节点的所有直属子节点（家族中某个长辈的所有直系子孙）。
```
select * from tb_menu m start with m.id=1 connect by m.pid=prior m.id;
```

4）查找一个节点的直属父节点（家族中的父亲），此时查找的是节点的直属父节点，也是用不到树形查询的
```
select c.id, c.title, p.id parent_id, p.title parent_title
    from tb_menu c, tb_menu p
    where c.pid=p.id and c.id=6
```

5）查找一个节点的所有直属父节点（家族中一个孩子的所有直系长辈祖先，比如父亲，祖父，……）
```
select * from tb_menu m start with m.id=38 connect by prior m.pid=m.id;
```

说明：      
上面的3）和5），这两条查询都是树形查询，区别在于”prior”关键字的位置不同，所以决定了查询方式的不同，查询结果也不同。当 pid = prior id时，数据库会根据当前的id迭代出pid与该id相同的记录，所以查询的结果是迭代出了所有的子类记录；而prior pid = id时，数据库会跟据当前的pid来迭代出与当前的pid相同的id的记录，所以查询出来的结果就是所有的父类结果。
仔细看一下数据库的查询结果，可以发现，其实3）和5）的查询顺序也是不同的，3）是自上向下检索，5）是自下向上检索。

6)查询一个节点的兄弟节点（亲兄弟）
```
--m.parent=m2.parent-->同一个父亲
select * from tb_menu m
where exists (select * from tb_menu m2 where m.pid=m2.pid and m2.id=6)
```

7)查询与一个节点同级的节点（族兄弟）。 如果在表中设置了级别的字段，那么在做这类查询时会很轻松，同一级别的就是与那个节点同级的，在这里列出不使用该字段时的实现!
```
with tmp as
 (select a.*, level leaf
    from tb_menu a
   start with a.pid = 0
  connect by a.pid = prior a.id)
select * from tmp where leaf = (select leaf from tmp where id = 50);
```

这里使用两个技巧，一个是使用了level来标识每个节点在表中的级别，还有就是使用with语法模拟出了一张带有级别的临时表。    
8)查询一个节点的父节点的的兄弟节点（伯父与叔父）
```
with tmp as(
    select tb_menu.*, level lev
    from tb_menu
    start with pid=0
    connect by pid = prior id) 

select b.*
from tmp b,(select *
            from tmp
            where id = 21 and lev = 2) a
where b.lev = 1

union all

select *
from tmp
where pid = (select distinct x.id
                from tmp x, --祖父
                     tmp y, --父亲
                     (select *
                      from tmp
                      where id = 21 and lev > 2) z --儿子
                where y.id = z.pid and x.id = y.pid); 
```

这里查询分成以下几步。
- 首先，和第7个一样，将全表都使用临时表加上级别；
- 其次，根据级别来判断有几种类型，以上文中举的例子来说，有三种情况：
    - （1）当前节点为顶级节点，即查询出来的lev值为1，那么它没有上级节点，不予考虑。
    - （2）当前节点为2级节点，查询出来的lev值为2，那么就只要保证lev级别为1的就是其上级节点的兄弟节点。
    - （3）其它情况就是3以及以上级别，那么就要选查询出来其上级的上级节点（祖父），再来判断祖父的下级节点都是属于该节点的上级节点的兄弟节点。
- 最后，就是使用union将查询出来的结果进行结合起来，形成结果集。

