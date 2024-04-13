-- Table: public.layer_source

-- DROP TABLE IF EXISTS public.layer_source;

CREATE TABLE IF NOT EXISTS public.layer_source
(
    source_id integer NOT NULL,
    layer_id integer,
    table_name text COLLATE pg_catalog."default",
    CONSTRAINT layer_source_pkc PRIMARY KEY (source_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.layer_source
    OWNER to postgres;

COMMENT ON TABLE public.layer_source
    IS 'レイヤソースマスタテーブル.ここで定義されているレイヤについて、グラフ・テーブルの設定を可能とする.';

COMMENT ON COLUMN public.layer_source.source_id
    IS 'ソースID';

COMMENT ON COLUMN public.layer_source.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.layer_source.table_name
    IS 'テーブル名:レイヤソースとなるテーブル名';