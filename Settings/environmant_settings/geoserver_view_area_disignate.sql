--エリア指定viewSQL
--7-24行目は表示する統計項目に合わせて修正してください。統計項目のカテゴリを増やす場合32行目を修正してください。
--25行目のリンクには、metabaseで作成した地域統計情報の共有URLを設定してください。
--40行目のWHERE句で表示するエリア区分を指定してください。
SELECT 
  ST_Union(tmp.geom) AS geom, 
  SUM(CASE tmp.項目 WHEN '人口_H30' THEN tmp.数 ELSE NULL END) AS "人口（H30）" ,
  SUM(CASE tmp.項目 WHEN '人口_R1' THEN tmp.数 ELSE NULL END) AS "人口（R1）" ,
  SUM(CASE tmp.項目 WHEN '人口_R2' THEN tmp.数 ELSE NULL END) AS "人口（R2）" ,
  SUM(CASE tmp.項目 WHEN '人口_R3' THEN tmp.数 ELSE NULL END) AS "人口（R3）" ,
  SUM(CASE tmp.項目 WHEN '人口_R4' THEN tmp.数 ELSE NULL END) AS "人口（R4）" ,
  SUM(CASE tmp.項目 WHEN '世帯_H30' THEN tmp.数 ELSE NULL END) AS "世帯数（H30）", 
  SUM(CASE tmp.項目 WHEN '世帯_R1' THEN tmp.数 ELSE NULL END) AS "世帯数（R1）", 
  SUM(CASE tmp.項目 WHEN '世帯_R2' THEN tmp.数 ELSE NULL END) AS "世帯数（R2）", 
  SUM(CASE tmp.項目 WHEN '世帯_R3' THEN tmp.数 ELSE NULL END) AS "世帯数（R3）", 
  SUM(CASE tmp.項目 WHEN '世帯_R4' THEN tmp.数 ELSE NULL END) AS "世帯数（R4）", 
  SUM(CASE tmp.項目 WHEN '事業所_H21' THEN tmp.数 ELSE NULL END) AS "事業所数（H21）", 
  SUM(CASE tmp.項目 WHEN '事業所_H24' THEN tmp.数 ELSE NULL END) AS "事業所数（H24）",
  SUM(CASE tmp.項目 WHEN '事業所_H26' THEN tmp.数 ELSE NULL END) AS "事業所数（H26）",
  SUM(CASE tmp.項目 WHEN '事業所_H28' THEN tmp.数 ELSE NULL END) AS "事業所数（H28）" ,
  SUM(CASE tmp.項目 WHEN '従業者数_H21' THEN tmp.数 ELSE NULL END) AS "従業者数（H21）" ,
   SUM(CASE tmp.項目 WHEN '従業者数_H24' THEN tmp.数 ELSE NULL END) AS "従業者数（H24）" ,
    SUM(CASE tmp.項目 WHEN '従業者数_H26' THEN tmp.数 ELSE NULL END) AS "従業者数（H26）" ,
     SUM(CASE tmp.項目 WHEN '従業者数_H28' THEN tmp.数 ELSE NULL END) AS "従業者数（H28）", 
'<a href="/metabase/public/dashboard/xxxxxxxxx" target="_metabase">閲覧</a>' AS "ダッシュボード" 
FROM 
(
  SELECT 
    geom, 
    t2.area_management_type AS エリア,
    t1.地点名 AS 地点名, 
    カテゴリ || '_' ||和暦 AS 項目,
    数 
  FROM gis_joint2 AS t1
  LEFT JOIN chochomokukai_erimane AS t2 
  ON t1.地点名 = t2.s_name
  WHERE "カテゴリ" IN ('人口', '世帯', '事業所', '従業者数') 
  ORDER BY "地点名", "カテゴリ", "西暦"
) AS tmp
WHERE tmp.エリア = 'キタ'
