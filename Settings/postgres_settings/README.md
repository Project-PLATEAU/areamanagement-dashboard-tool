# Postgresセットアップ手順

1. DBを作成後、psqlからカレントディレクトリを直下に変更する
```
\cd /xxxx/xxxx/xxxx/postgres_settings
```
2. DDLの一括sqlを実行
```
\i 1.all_ddl_setup.sql
```
3. VIEWの一括sqlを実行
```
\i 2.all_view_setup.sql
```
4. DMLの一括sqlを実行
```
\i 3.all_dml_setup.sql
```
5. サンプルデータの取り込み
```
\i 4.sample_data_copy.sql
```