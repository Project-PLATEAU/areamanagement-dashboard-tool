SELECT 
     link_id AS "リンクID",
	 CASE rt_struct
	   WHEN 1 THEN '車道と歩道の物理的な分離あり'
       WHEN 2 THEN '車道と歩道の物理的な分離なし'
       WHEN 3 THEN '横断歩道'
       WHEN 4 THEN '横断歩道の路面標示の無い道路の横断部'
       WHEN 5 THEN '地下通路'
       WHEN 6 THEN '歩道橋'
       WHEN 7 THEN '施設内通路'
       WHEN 8 THEN 'その他の経路の構造'
       WHEN 99 THEN '不明'
       ELSE '不明' END AS 経路の構造,
     CASE route_type
	   WHEN 1 THEN '対応する属性情報なし'
       WHEN 2 THEN '動く歩道'
       WHEN 3 THEN '踏切'
       WHEN 4 THEN 'エレベーター'
       WHEN 5 THEN 'エスカレーター'
       WHEN 6 THEN '階段'
       WHEN 7 THEN 'スロープ'
       WHEN 99 THEN '不明'
       ELSE '不明' END AS 経路の種別, 
	 CASE direction
	   WHEN 1 THEN '両方向'
       WHEN 2 THEN '起点より終点方向'
       WHEN 3 THEN '終点より起点方向'
       WHEN 99 THEN '不明'
       ELSE '不明' END AS 方向性, 
	 CASE width
	   WHEN 1 THEN '1.0m 未満'
       WHEN 2 THEN '1.0m 以上～2.0m 未満'
       WHEN 3 THEN '2.0m以上～3.0m 未満'
       WHEN 4 THEN '3.0m 以上'
       WHEN 99 THEN '不明'
	   ELSE '不明' END AS 幅員, 
	 CASE vtcl_slope
	   WHEN 1 THEN '5％以下'
       WHEN 2 THEN '5％より大きい（起点より終点が高い）'
       WHEN 3 THEN '5％より大きい（起点より終点が低い）'
       WHEN 99 THEN '不明'
	   ELSE '不明' END AS 縦断勾配, 
	 CASE lev_diff
	    WHEN 1 THEN '2 ㎝以下'
        WHEN 2 THEN '2 ㎝より大きい'
        WHEN 99 THEN '不明'
	    ELSE '不明' END AS 段差, 
	CASE tfc_signal
	    WHEN 1 THEN '歩行者用信号機なし'
        WHEN 2 THEN '歩車分離式信号機あり'
        WHEN 3 THEN '押しボタン式信号機あり'
        WHEN 4 THEN 'これら以外の信号機'
        WHEN 99 THEN '不明'
	    ELSE '不明' END AS 歩行者用信号機の有無, 
	CASE tfc_signal 
	    WHEN 1 THEN '音響設備なし'
        WHEN 2 THEN '音響設備あり（音響用押しボタンなし）'
        WHEN 3 THEN '音響設備あり（音響用押しボタンあり）'
        WHEN 4 THEN 'これら以外の信号機'
        WHEN 99 THEN '不明'
	    ELSE '不明' END AS 歩行者用信号機の種別, 
	CASE brail_tile
	    WHEN 1 THEN '視覚障害者誘導用ブロック等なし'
        WHEN 2 THEN '視覚障害者誘導用ブロック等あり'
        WHEN 99 THEN '不明'
	    ELSE '不明' END AS 視覚障害者誘導用ブロック等の有無, 
	CASE elevator
	    WHEN 1 THEN 'エレベーターなし'
        WHEN 2 THEN 'エレベーターあり（バリアフリー対応なし）'
        WHEN 3 THEN 'エレベーターあり（車いす使用者対応）'
        WHEN 4 THEN 'エレベーターあり（視覚障害者対応）'
        WHEN 5 THEN 'エレベーターあり（車いす使用者、視覚障害者対応）'
        WHEN 99 THEN '不明'
	    ELSE '不明' END AS エレベーターの種別, 
	CASE roof 
	    WHEN 1 THEN 'なし'
        WHEN 2 THEN 'あり'
        WHEN 99 THEN '不明'
	    ELSE '不明' END AS 屋根の有無, 
	geom
FROM link_3d
