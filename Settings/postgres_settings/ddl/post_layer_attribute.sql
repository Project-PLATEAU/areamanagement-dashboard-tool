-- Table: public.post_layer_attribute

-- DROP TABLE IF EXISTS public.post_layer_attribute;

CREATE TABLE IF NOT EXISTS public.post_layer_attribute
(
    layer_id integer NOT NULL,
    item_id integer NOT NULL,
    item_name character varying(100) COLLATE pg_catalog."default",
    item_type integer,
    disp_order integer,
    require_flag character(1) COLLATE pg_catalog."default",
    CONSTRAINT post_layer_attribule_pkc PRIMARY KEY (layer_id, item_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.post_layer_attribute
    OWNER to postgres;

COMMENT ON TABLE public.post_layer_attribute
    IS '投稿レイヤ属性マスタテーブル';

COMMENT ON COLUMN public.post_layer_attribute.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.post_layer_attribute.item_id
    IS '項目ID';

COMMENT ON COLUMN public.post_layer_attribute.item_name
    IS '項目名';

COMMENT ON COLUMN public.post_layer_attribute.item_type
    IS '項目タイプ:1: テキスト(小) 2: テキスト(大) 3:数値 4:写真 5：日付';

COMMENT ON COLUMN public.post_layer_attribute.disp_order
    IS '表示順';

COMMENT ON COLUMN public.post_layer_attribute.require_flag
    IS '必須フラグ:1:必須 0:任意';