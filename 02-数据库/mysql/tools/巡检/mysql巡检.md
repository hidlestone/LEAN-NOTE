# mysql巡检

### 一、巡检内容

表巡检
- 大小超过10G的表
- 索引超过6个的表
- 碎片率超过50%的表
- 行数超过1000万行的表
- 非默认字符集的表
- 含有大字段的表
- varchar定义超长的表
- 无主键/索引的表

索引巡检
- 重复索引
- 索引列超过5个的索引
- 无用索引

重要参数
- version
- innodb_buffer_pool_size
- innodb_flush_log_at_trx_commit
- innodb_log_file_size
- innodb_log_files_in_group
- innodb_file_per_table
- innodb_max_dirty_pages_pct
- sync_binlog
- max_connections
- query_cache_type
- table_open_cache
- table_definition_cache

重要状态指标
- Uptime
- Opened_files
- Opened_table_definitions
- Opened_tables
- Max_used_connections
- Threads_created
- Threads_connected
- Aborted_connects
- Aborted_clients
- Table_locks_waited
- Innodb_buffer_pool_wait_free
- Innodb_log_waits
- Table_locks_waited
- Innodb_row_lock_waits
- Innodb_row_lock_time_avg
- Binlog_cache_disk_use
- Created_tmp_disk_tables

用户检查
- 无密码用户
- %用户

权限检查


### 二、使用脚本
mysql/tools/巡检/check_mysql.py

创建巡检用户
```sql
grant select,process on *.* to monitor@localhost identified by '123456'
```

巡检脚本填入相应ip、端口号、账号、密码

执行巡检
