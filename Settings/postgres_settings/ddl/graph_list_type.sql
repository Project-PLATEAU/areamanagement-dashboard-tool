-- Table: public.graph_list_type

-- DROP TABLE IF EXISTS public.graph_list_type;

CREATE TABLE IF NOT EXISTS public.graph_list_type
(
    graph_type_id integer NOT NULL,
    graph_type_name character varying(100) COLLATE pg_catalog."default",
    edit_flag character(1) COLLATE pg_catalog."default",
    default_query_text text COLLATE pg_catalog."default",
    CONSTRAINT graph_list_type_pkc PRIMARY KEY (graph_type_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.graph_list_type
    OWNER to postgres;

COMMENT ON TABLE public.graph_list_type
    IS 'グラフ・リストタイプのマスタテーブル';

COMMENT ON COLUMN public.graph_list_type.graph_type_id
    IS 'グラフ・リストタイプID.
1:複合グラフ
2:円グラフ
3:棒グラフ
4:線グラフ
5:リスト(テーブル)
6:単一リスト';

COMMENT ON COLUMN public.graph_list_type.graph_type_name
    IS 'グラフ・リストタイプ名称';

COMMENT ON COLUMN public.graph_list_type.edit_flag
    IS '編集フラグ:1:編集可能 0:編集不可 ※変更不可';

COMMENT ON COLUMN public.graph_list_type.default_query_text
    IS 'グラフ・リストタイプ毎の雛形SQL ※変更不可';