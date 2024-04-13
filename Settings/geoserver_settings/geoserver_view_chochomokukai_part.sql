SELECT t1.geom, t1.city_name AS 市区名, t1.area_management_type AS エリマネ団体 
FROM chochomokukai_erimane AS t1
WHERE t1.s_area = '%s_area%'
