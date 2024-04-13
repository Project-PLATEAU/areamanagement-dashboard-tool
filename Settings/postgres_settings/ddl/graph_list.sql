-- Table: public.graph_list

-- DROP TABLE IF EXISTS public.graph_list;

CREATE TABLE IF NOT EXISTS public.graph_list
(
    graph_id integer NOT NULL,
    graph_type_id integer NOT NULL,
    graph_name character varying(100) COLLATE pg_catalog."default",
    query_text text COLLATE pg_catalog."default",
    edit_flag character(1) COLLATE pg_catalog."default",
    source_id integer,
    placeholder_flag character(1) COLLATE pg_catalog."default",
    CONSTRAINT graph_list_pkc PRIMARY KEY (graph_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.graph_list
    OWNER to postgres;

COMMENT ON TABLE public.graph_list
    IS 'グラフ・リストのマスタテーブル';

COMMENT ON COLUMN public.graph_list.graph_id
    IS 'グラフ・リストID';

COMMENT ON COLUMN public.graph_list.graph_type_id
    IS 'グラフ・リストタイプID.
1:複合グラフ
2:円グラフ
3:棒グラフ
4:線グラフ
5:リスト(テーブル)
6:単一リスト';

COMMENT ON COLUMN public.graph_list.graph_name
    IS 'グラフ名称';

COMMENT ON COLUMN public.graph_list.query_text
    IS 'クエリテキスト:データ取得用のSQL文';

COMMENT ON COLUMN public.graph_list.edit_flag
    IS '編集フラグ:1:編集可能 0:編集不可 ※SE設定の場合0';

COMMENT ON COLUMN public.graph_list.source_id
    IS 'グラフ・リスト作成画面で生成された場合のみ使用 ※SE設定の場合NULL';

COMMENT ON COLUMN public.graph_list.placeholder_flag
    IS 'query_textにプレースホルダを使用している場合:1 ,使用していない場合:0';