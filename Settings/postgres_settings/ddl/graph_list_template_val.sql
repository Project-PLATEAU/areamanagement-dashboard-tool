-- Table: public.graph_list_template_val

-- DROP TABLE IF EXISTS public.graph_list_template_val;

CREATE TABLE IF NOT EXISTS public.graph_list_template_val
(
    graph_id integer NOT NULL,
    item_id integer NOT NULL,
    item_value text COLLATE pg_catalog."default",
    CONSTRAINT graph_list_template_val_pkc PRIMARY KEY (graph_id, item_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.graph_list_template_val
    OWNER to postgres;

COMMENT ON TABLE public.graph_list_template_val
    IS 'グラフ・リストテンプレート設定項目値マスタテーブル';

COMMENT ON COLUMN public.graph_list_template_val.graph_id
    IS 'グラフ・リストID';

COMMENT ON COLUMN public.graph_list_template_val.item_id
    IS '項目ID';

COMMENT ON COLUMN public.graph_list_template_val.item_value
    IS '項目値';