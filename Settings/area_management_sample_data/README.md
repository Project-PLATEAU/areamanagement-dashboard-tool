# サンプルデータの取り込み手順

1. Postgres環境設定完了後、psqlからカレントディレクトリを直下に変更する
```
\cd /xxxx/xxxx/xxxx/area_management_sample_data
```
2. サンプルデータの取り込み
```
\i sample_data_copy.sql
```

※shapeからDBへの取り込みの手順及び詳細は構築手順書を参照。