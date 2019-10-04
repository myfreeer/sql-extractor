# sql-extractor
Export data from oracle database to plain sql script.

Note: this would not generate ddl.

## Prerequisites
* java 8+
* maven 3+
* Manaully get [oracle jdbc driver](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html), 
rename the jar to `ojdbc6-11.2.0.3.jar` and put it in `lib/com/oracle/ojdbc6/11.2.0.3/`

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
  # path to save sql script
  file-path: C:\export.sql
  # Export type
  # full: export all tables in current schema, excluding exclude-tables
  # table: export specified tables in tables, excluding exclude-tables
  type: full
  # Tables to export, effective if type is table
  exclude-tables:
    - exclude_table_1
  # Tables to exclude, effective if type is table or full
  tables:
    - include_table_1

```
