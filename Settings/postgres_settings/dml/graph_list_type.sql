insert into public.graph_list_type(graph_type_id,graph_type_name,edit_flag,default_query_text) values 
    (1,'複合グラフ','0',null)
  , (2,'円グラフ','1','/*_auto_query_identifier_*/ SELECT $YColumnPlaceHoderName$,$XColumnPlaceHoderName$,{{LonLatPlaceHoderName}} FROM $TablePlaceHoderName$ WHERE {{YColumnPlaceHoderName}} IS NOT NULL {{GroupBy}} ORDER BY {{SortMode}} LIMIT {{LimitSize}};')
  , (3,'棒グラフ','1','/*_auto_query_identifier_*/ SELECT $YColumnPlaceHoderName$,$XColumnPlaceHoderName$,{{LonLatPlaceHoderName}} FROM $TablePlaceHoderName$ WHERE {{YColumnPlaceHoderName}} IS NOT NULL {{GroupBy}} ORDER BY {{SortMode}} LIMIT {{LimitSize}};')
  , (4,'線グラフ','1','/*_auto_query_identifier_*/ SELECT $YColumnPlaceHoderName$,$XColumnPlaceHoderName$,{{LonLatPlaceHoderName}} FROM $TablePlaceHoderName$ WHERE {{YColumnPlaceHoderName}} IS NOT NULL {{GroupBy}} ORDER BY {{SortMode}} LIMIT {{LimitSize}};')
  , (5,'リスト（テーブル）','1','/*_auto_query_identifier_*/ SELECT $ColumnListPlaceHoderName$,{{LonLatPlaceHoderName}} FROM $TablePlaceHoderName$ WHERE {{ConditionsForPublishFlag}} ORDER BY {{SortMode}} LIMIT {{LimitSize}};')
  , (6,'単一リスト','0',null);
