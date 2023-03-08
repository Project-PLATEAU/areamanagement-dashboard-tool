--人気スポットラベルviewSQL
-- 回数毎に作成する場合は「initview=1」「accessspot_1」を回数に置き換えてください
-- (例)2回目の場合:「initview=2」「accessspot_2」
SELECT id, スポット名, ST_Force3D(geom, (合計 + 60.0)) AS geom , '<a href="/migratoryinfo/#initview=1" target="_metabase">閲覧</a>' AS "ダッシュボード" FROM accessspot_1
