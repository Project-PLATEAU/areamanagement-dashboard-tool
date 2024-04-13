-- Table: public.layer_source_field

-- DROP TABLE IF EXISTS public.layer_source_field;

CREATE TABLE IF NOT EXISTS public.layer_source_field
(
    field_id integer NOT NULL,
    source_id integer,
    field_name text COLLATE pg_catalog."default",
    alias text COLLATE pg_catalog."default",
    CONSTRAINT layer_source_field_pkc PRIMARY KEY (field_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.layer_source_field
    OWNER to postgres;

COMMENT ON TABLE public.layer_source_field
    IS 'レイヤソース_フィールド定義テーブル.レイヤソースのカラム毎に定義.';

COMMENT ON COLUMN public.layer_source_field.field_id
    IS 'フィールドID';

COMMENT ON COLUMN public.layer_source_field.source_id
    IS 'ソースID';

COMMENT ON COLUMN public.layer_source_field.field_name
    IS 'フィールド名:テーブルの物理エンティティ名';

COMMENT ON COLUMN public.layer_source_field.alias
    IS 'エイリアス:テーブルの論理エンティティ名';