--町丁目viewSQL
--7-24行目は表示する統計項目に合わせて修正してください。統計項目のカテゴリを増やす場合32行目を修正してください。
--36行目のリンクには、metabaseで作成した地域統計情報の共有URLを設定してください。

WITH area_statistics AS (SELECT 
  tmp.地点名 AS 町丁目名,
  MAX(CASE tmp.項目 WHEN '人口_H30' THEN tmp.数 ELSE NULL END) AS "人口（H30）" ,
  MAX(CASE tmp.項目 WHEN '人口_R1' THEN tmp.数 ELSE NULL END) AS "人口（R1）" ,
  MAX(CASE tmp.項目 WHEN '人口_R2' THEN tmp.数 ELSE NULL END) AS "人口（R2）" ,
  MAX(CASE tmp.項目 WHEN '人口_R3' THEN tmp.数 ELSE NULL END) AS "人口（R3）" ,
  MAX(CASE tmp.項目 WHEN '人口_R4' THEN tmp.数 ELSE NULL END) AS "人口（R4）" ,
  MAX(CASE tmp.項目 WHEN '世帯_H30' THEN tmp.数 ELSE NULL END) AS "世帯数（H30）", 
  MAX(CASE tmp.項目 WHEN '世帯_R1' THEN tmp.数 ELSE NULL END) AS "世帯数（R1）", 
  MAX(CASE tmp.項目 WHEN '世帯_R2' THEN tmp.数 ELSE NULL END) AS "世帯数（R2）", 
  MAX(CASE tmp.項目 WHEN '世帯_R3' THEN tmp.数 ELSE NULL END) AS "世帯数（R3）", 
  MAX(CASE tmp.項目 WHEN '世帯_R4' THEN tmp.数 ELSE NULL END) AS "世帯数（R4）", 
  MAX(CASE tmp.項目 WHEN '事業所_H21' THEN tmp.数 ELSE NULL END) AS "事業所数（H21）", 
  MAX(CASE tmp.項目 WHEN '事業所_H24' THEN tmp.数 ELSE NULL END) AS "事業所数（H24）",
  MAX(CASE tmp.項目 WHEN '事業所_H26' THEN tmp.数 ELSE NULL END) AS "事業所数（H26）",
  MAX(CASE tmp.項目 WHEN '事業所_H28' THEN tmp.数 ELSE NULL END) AS "事業所数（H28）" ,
  MAX(CASE tmp.項目 WHEN '従業者数_H21' THEN tmp.数 ELSE NULL END) AS "従業者数（H21）" ,
   MAX(CASE tmp.項目 WHEN '従業者数_H24' THEN tmp.数 ELSE NULL END) AS "従業者数（H24）" ,
    MAX(CASE tmp.項目 WHEN '従業者数_H26' THEN tmp.数 ELSE NULL END) AS "従業者数（H26）" ,
     MAX(CASE tmp.項目 WHEN '従業者数_H28' THEN tmp.数 ELSE NULL END) AS "従業者数（H28）"
FROM 
(
  SELECT 
    地点名,  
    カテゴリ || '_' ||和暦 AS 項目,
    数 
  FROM gis_joint2 
  WHERE "カテゴリ" IN ('人口', '世帯', '事業所', '従業者数') ORDER BY "地点名", "カテゴリ", "西暦"
) AS tmp
GROUP BY tmp.地点名)
SELECT t1.geom, t1.city_name AS 市区名, t1.area_management_type AS エリマネ団体, t2.*, 
'<a href="/metabase/public/dashboard/xxxxxxxxx" target="_metabase">閲覧</a>' AS "ダッシュボード" 
FROM chochomokukai_erimane AS t1
LEFT JOIN area_statistics AS t2 
ON t1.s_name = t2.町丁目名 
WHERE t1.s_area = '%s_area%'