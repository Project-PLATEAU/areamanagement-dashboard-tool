COPY public.theme (theme_id, theme_name, theme_group_name, disp_order, publish_flag, post_flag, information_text, switch_flag, switch_query, switch_item_name_column_name, switch_item_value_column_name, switch_placeholder_name, switch_placeholder_default_value) FROM stdin;
1	エリマネ活動サンプル	エリマネ・イベント活動	1	1	1	<div style="height:1500px">\n<h1>HTML Ipsum Presents</h1>\n<p><strong>Pellentesque habitant morbi tristique</strong> senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. <em>Aenean ultricies mi vitae est.</em> Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, <code>commodo vitae</code>, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. <a href="#">Donec non enim</a> in turpis pulvinar facilisis. Ut felis.</p>\n<h2>Header Level 2</h2>\n<ol>\n  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n  <li>Aliquam tincidunt mauris eu risus.</li>\n</ol>\n<blockquote>\n  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus magna. Cras in mi at felis aliquet congue. Ut a est eget ligula molestie gravida. Curabitur massa. Donec eleifend, libero at sagittis mollis, tellus est malesuada tellus, at luctus turpis elit sit amet quam. Vivamus pretium ornare est.</p>\n</blockquote>\n<h3>Header Level 3</h3>\n<ul>\n  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n  <li>Aliquam tincidunt mauris eu risus.</li>\n</ul>\n</div>	0	\N	\N	\N	\N	\N
2	イベント活動サンプル	エリマネ・イベント活動	2	1	1	<div style="height:1500px">\n<h1>HTML Ipsum Presents</h1>\n<p><strong>Pellentesque habitant morbi tristique</strong> senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. <em>Aenean ultricies mi vitae est.</em> Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, <code>commodo vitae</code>, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. <a href="#">Donec non enim</a> in turpis pulvinar facilisis. Ut felis.</p>\n<h2>Header Level 2</h2>\n<ol>\n  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n  <li>Aliquam tincidunt mauris eu risus.</li>\n</ol>\n<blockquote>\n  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus magna. Cras in mi at felis aliquet congue. Ut a est eget ligula molestie gravida. Curabitur massa. Donec eleifend, libero at sagittis mollis, tellus est malesuada tellus, at luctus turpis elit sit amet quam. Vivamus pretium ornare est.</p>\n</blockquote>\n<h3>Header Level 3</h3>\n<ul>\n  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n  <li>Aliquam tincidunt mauris eu risus.</li>\n</ul>\n</div>	0	\N	\N	\N	\N	\N
3	投稿レイヤサンプル	投稿レイヤ	1	1	1	<div style="height:1500px">\n<h1>HTML Ipsum Presents</h1>\n<p><strong>Pellentesque habitant morbi tristique</strong> senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. <em>Aenean ultricies mi vitae est.</em> Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, <code>commodo vitae</code>, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. <a href="#">Donec non enim</a> in turpis pulvinar facilisis. Ut felis.</p>\n<h2>Header Level 2</h2>\n<ol>\n  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n  <li>Aliquam tincidunt mauris eu risus.</li>\n</ol>\n<blockquote>\n  <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus magna. Cras in mi at felis aliquet congue. Ut a est eget ligula molestie gravida. Curabitur massa. Donec eleifend, libero at sagittis mollis, tellus est malesuada tellus, at luctus turpis elit sit amet quam. Vivamus pretium ornare est.</p>\n</blockquote>\n<h3>Header Level 3</h3>\n<ul>\n  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li>\n  <li>Aliquam tincidunt mauris eu risus.</li>\n</ul>\n</div>	0	\N	\N	\N	\N	\N
\.

COPY public.theme_graph_list (theme_id, graph_id, top_left_x, top_left_y, panel_width, panel_height) FROM stdin;
1	-1	4	0	8	22
2	-1	4	0	8	22
3	-1	4	0	8	22
1	1	0	3	4	11
1	2	0	14	4	16
1	3	0	0	4	3
2	4	0	3	4	11
2	5	0	14	4	13
2	6	0	0	4	3
3	7	0	3	4	17
3	8	0	0	4	3
\.

COPY public.theme_layer (theme_id, layer_id, disp_order, post_flag) FROM stdin;
1	9	1	1
2	10	1	1
3	1	1	1
\.

COPY public.layer_graph_cooporation (cooperation_id, cooperation_type, cooperation_option, layer_id, graph_id) FROM stdin;
1	0	{"featureAttributeName":"_","featureAttributeColumnName":"_","longitudeColumnName":"longitude","latitudeColumnName":"latitude"}	9	2
2	0	{"featureAttributeName":"_","featureAttributeColumnName":"_","longitudeColumnName":"longitude","latitudeColumnName":"latitude"}	10	5
3	0	{"featureAttributeName":"_","featureAttributeColumnName":"_","longitudeColumnName":"longitude","latitudeColumnName":"latitude"}	1	7
\.

COPY public.graph_list_template_val (graph_id, item_id, item_value) FROM stdin;
1	1	参加者数
1	2	活動場所
2	2	{"参加者数":7,"備考":10,"終了日時":9,"開始日時":8,"活動内容":6,"活動名":4,"活動ID":1,"団体名":3,"地域活動種別名":2,"活動場所":5}
2	100	 "activity_id" as "活動ID" , "activity_type_name" as "地域活動種別名" , "group_type_name" as "団体名" , "activity_name" as "活動名" , "activity_place" as "活動場所" , "activity_content" as "活動内容" , "participants_count" as "参加者数" , "start_date_and_time" as "開始日時" , "end_date_and_time" as "終了日時" , "remarks" as "備考" 
2	102	activity_view_for_erimane
1	4	#7172AD,#88BF4D,#98D9D9,#A989C5,#F9D45C,#509EE3,#EF8C8C,#efce8c,#b5f95c,#F2A86F,#50e3ae
3	1	count
4	1	参加者数
4	2	活動場所
4	4	#7172AD,#88BF4D,#98D9D9,#A989C5,#F9D45C,#509EE3,#EF8C8C,#efce8c,#b5f95c,#F2A86F,#50e3ae
5	2	{"参加者数":7,"備考":10,"終了日時":9,"開始日時":8,"活動内容":6,"活動名":4,"活動ID":1,"団体名":3,"地域活動種別名":2,"活動場所":5}
5	100	 "activity_id" as "活動ID" , "activity_type_name" as "地域活動種別名" , "group_type_name" as "団体名" , "activity_name" as "活動名" , "activity_place" as "活動場所" , "activity_content" as "活動内容" , "participants_count" as "参加者数" , "start_date_and_time" as "開始日時" , "end_date_and_time" as "終了日時" , "remarks" as "備考" 
6	1	count
5	102	activity_view_for_event
7	102	post_layer_feature_1
8	1	count
7	2	{"投稿日時":2,"備考5":11,"属性ID":1,"投稿内容":4,"備考6":12,"備考3":9,"備考4":10,"備考1":7,"タイトル":3,"備考2":8,"投稿写真1":5,"投稿写真2":6}
7	100	 "feature_id" as "属性ID" , "post_datetime" as "投稿日時" , "item_1" as "タイトル" , "item_2" as "投稿内容" , "item_3" as "投稿写真1" , "item_4" as "投稿写真2" , "item_5" as "備考1" , "item_6" as "備考2" , "item_7" as "備考3" , "item_8" as "備考4" , "item_9" as "備考5" , "item_10" as "備考6" 
\.

COPY public.graph_list (graph_id, graph_type_id, graph_name, query_text, edit_flag, source_id, placeholder_flag) FROM stdin;
2	5	エリマネ活動-活動一覧	/*_auto_query_identifier_*/ SELECT $ColumnListPlaceHoderName$, ((ST_XMax(ST_TransForm(geom, 4326))+ST_XMin(ST_TransForm(geom, 4326)))/2) AS longitude  , ((ST_YMax(ST_TransForm(geom, 4326))+ST_YMin(ST_TransForm(geom, 4326)))/2) AS latitude  FROM $TablePlaceHoderName$ WHERE  "publish_flag" = '1'  ORDER BY  "活動ID"  DESC  LIMIT 1000;	1	9	1
1	2	エリマネ活動-活動場所別参加者数割合	SELECT  SUM(CASE WHEN isnumericex(cast("participants_count" as text)) THEN cast("participants_count" as numeric) ELSE 0 END) as "参加者数" ,"activity_place" as "活動場所"  FROM activity_view_for_erimane WHERE  "publish_flag" = '1'  AND "participants_count" IS NOT NULL  GROUP BY "activity_place"  ORDER BY  "参加者数"  DESC  LIMIT 10;	0	\N	0
3	6	エリマネ活動-投稿件数	select to_char(count(*), 'fm999,999,999,999') || '件' AS count from activity_view_for_erimane WHERE  publish_flag = '1'	0	\N	0
5	5	イベント活動-活動一覧	/*_auto_query_identifier_*/ SELECT $ColumnListPlaceHoderName$, ((ST_XMax(ST_TransForm(geom, 4326))+ST_XMin(ST_TransForm(geom, 4326)))/2) AS longitude  , ((ST_YMax(ST_TransForm(geom, 4326))+ST_YMin(ST_TransForm(geom, 4326)))/2) AS latitude  FROM $TablePlaceHoderName$ WHERE  "publish_flag" = '1'  ORDER BY  "活動ID"  DESC  LIMIT 1000;	1	10	1
4	2	イベント活動-活動場所別参加者数割合	SELECT  SUM(CASE WHEN isnumericex(cast("participants_count" as text)) THEN cast("participants_count" as numeric) ELSE 0 END) as "参加者数" ,"activity_place" as "活動場所"  FROM activity_view_for_event WHERE  "publish_flag" = '1'  AND "participants_count" IS NOT NULL  GROUP BY "activity_place"  ORDER BY  "参加者数"  DESC  LIMIT 10;	0	\N	0
6	6	イベント活動-投稿件数	select to_char(count(*), 'fm999,999,999,999') || '件' AS count from activity_view_for_event WHERE  publish_flag = '1'	0	\N	0
7	5	投稿レイヤ-投稿一覧	/*_auto_query_identifier_*/ SELECT $ColumnListPlaceHoderName$, ((ST_XMax(ST_TransForm(geometry, 4326))+ST_XMin(ST_TransForm(geometry, 4326)))/2) AS longitude  , ((ST_YMax(ST_TransForm(geometry, 4326))+ST_YMin(ST_TransForm(geometry, 4326)))/2) AS latitude  FROM $TablePlaceHoderName$ WHERE  "publish_flag" = '1'  ORDER BY  "属性ID"  DESC  LIMIT 1000;	1	1	1
8	6	投稿レイヤ-投稿件数	select to_char(count(*), 'fm999,999,999,999') || '件' AS count from post_layer_feature_1 WHERE  publish_flag = '1'	0	\N	0
\.
