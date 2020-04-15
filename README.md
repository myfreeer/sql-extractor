# sql-extractor
Export data from oracle database to plain sql script.

Note: this would not generate ddl.

## Prerequisites
* java 8+
* maven 3+
* network access to maven central or its mirror 

## Config

```yaml
spring:
  datasource:
    # jdbc connection url
    url: jdbc:oracle:thin:@127.0.0.1:1521:orcl
    # jdbc connection user name
    username: SCOTT
    # jdbc connection password
    password: 123456

export:
  # Time zone for DateFormatter and TimestampFormatter
  # example: UTC, GMT+8
  time-zone: UTC
  # Locale for DateFormatter and TimestampFormatter
  # example: en-US, zh-CN
  locale: en-US
  # path to save sql script
  file-path: C:\export.sql
  # Export type
  # full: export all tables in current schema, excluding exclude-tables
  # table: export specified tables in tables, excluding exclude-tables
  # sql: export tables via custom sql select statement
  type: full
  # Tables to exclude, effective if type is table or full
  exclude-tables:
    - exclude_table_1
  # Tables to export, effective if type is table
  tables:
    - include_table_1
  # Sql select statements for exporting, effective if type is sql
  sql:
    table_name_1: select * from dual where rownum = 1

```
