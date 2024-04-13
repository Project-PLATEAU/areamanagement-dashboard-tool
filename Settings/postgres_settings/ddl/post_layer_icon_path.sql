-- Table: public.post_layer_icon_path

-- DROP TABLE IF EXISTS public.post_layer_icon_path;

CREATE TABLE IF NOT EXISTS public.post_layer_icon_path
(
    id integer NOT NULL DEFAULT nextval('post_layer_icon_path_id_seq'::regclass),
    layer_id integer NOT NULL,
    image_path text COLLATE pg_catalog."default",
    judgment_value text COLLATE pg_catalog."default",
    CONSTRAINT post_layer_icon_path_pkc PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.post_layer_icon_path
    OWNER to postgres;

COMMENT ON TABLE public.post_layer_icon_path
    IS '投稿レイヤの項目値_アイコン関連テーブル';

COMMENT ON COLUMN public.post_layer_icon_path.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.post_layer_icon_path.image_path
    IS 'アイコンパス';

COMMENT ON COLUMN public.post_layer_icon_path.judgment_value
    IS '判定値';