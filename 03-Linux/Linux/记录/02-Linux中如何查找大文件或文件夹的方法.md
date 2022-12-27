


超过800M大小的文件
```
find . -type f -size +800M

./flash_recovery_area/backup/backupsets/ora_df873519197_s46815_s1
```


超过800M大小的文件(详细信息)
```
 find . -type f -size +800M  -print0 | xargs -0 ls -l
 
 -rw-r----- 1 oracle oinstall 2782846976 Mar  6 11:51 ./flash_recovery_area/backup/backupsets/ora_df873513413_s46809_s1
```


当我们只需要查找超过800M大小文件，并显示查找出来文件的具体大小，可以使用下面命令
```
find . -type f -size +800M  -print0 | xargs -0 du -h

1.3G    ./flash_recovery_area/backup/backupsets/ora_df873519197_s46815_s1
```

如果你还需要对查找结果按照文件大小做一个排序，那么可以使用下面命令
```
find . -type f -size +800M  -print0 | xargs -0 du -h | sort -nr

8.1G    ./oradata/epps/undotbs01.dbf
4.1G    ./oradata/epps/invsubmat_d08.dbf
```

譬如有时候磁盘空间告警了，而你平时又疏于管理、监控文件的增长，那么我需要快速的了解哪些目录变得比较大，那么此时我们可以借助du命令来帮我们解决这个问题。
```
du -h --max-depth=1
16K     ./lost+found
33G     ./flash_recovery_area
37G     ./oradata
70G     .
```
如果你想知道flash_recovery_area目录下面有哪些大文件夹，那么可以将参数max-depth=2 ，如果你想对搜索出来的结果进行排序，那么可以借助于sort命令。如下所示
```
du -h --max-depth=2 | sort -n
3.5G    ./flash_recovery_area/EPPS
16K     ./lost+found
29G     ./flash_recovery_area/backup
33G     ./flash_recovery_area
37G     ./oradata
37G     ./oradata/epps
70G     .

du -hm --max-depth=2 | sort -n
1       ./lost+found
3527    ./flash_recovery_area/EPPS
29544   ./flash_recovery_area/backup
33070   ./flash_recovery_area
37705   ./oradata
37705   ./oradata/epps
70775   .
```



