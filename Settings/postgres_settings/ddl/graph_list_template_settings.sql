-- Table: public.graph_list_template_settings

-- DROP TABLE IF EXISTS public.graph_list_template_settings;

CREATE TABLE IF NOT EXISTS public.graph_list_template_settings
(
    graph_type_id integer NOT NULL,
    item_id integer NOT NULL,
    attribute_name character varying(100) COLLATE pg_catalog."default",
    attribute_type integer,
    display_name character varying(100) COLLATE pg_catalog."default",
    display_type integer,
    group_type integer,
    placeholder_flag character(1) COLLATE pg_catalog."default",
    CONSTRAINT graph_list_template_settings_pkc PRIMARY KEY (item_id, graph_type_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.graph_list_template_settings
    OWNER to postgres;

COMMENT ON TABLE public.graph_list_template_settings
    IS 'グラフ・リストテンプレート設定項目マスタテーブル';

COMMENT ON COLUMN public.graph_list_template_settings.item_id
    IS '項目ID';

COMMENT ON COLUMN public.graph_list_template_settings.graph_type_id
    IS 'グラフ・リストタイプID.※変更不可
1:複合グラフ
2:円グラフ
3:棒グラフ
4:線グラフ
5:リスト(テーブル)
6:単一リスト';

COMMENT ON COLUMN public.graph_list_template_settings.attribute_name
    IS '属性名:プログラムで使用する属性名.※変更不可';

COMMENT ON COLUMN public.graph_list_template_settings.attribute_type
    IS '属性タイプ.※変更不可
1:string
2:number
3:array[string]
4:array[number]
5:object';

COMMENT ON COLUMN public.graph_list_template_settings.display_name
    IS '設定項目の名称';

COMMENT ON COLUMN public.graph_list_template_settings.display_type
    IS '表示タイプ.※変更不可
1:文字列・数値
2:カンマ区切り
3:未使用
4:object';

COMMENT ON COLUMN public.graph_list_template_settings.group_type
    IS 'グループタイプ:設定項目のグルーピング ※未使用';
    
COMMENT ON COLUMN public.graph_list_template_settings.placeholder_flag
    IS 'graph_list_type.default_query_textでプレースホルダを使用する場合:1,使用しない場合:0 ※変更不可';