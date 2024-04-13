-- Table: public.theme_layer

-- DROP TABLE IF EXISTS public.theme_layer;

CREATE TABLE IF NOT EXISTS public.theme_layer
(
    theme_id integer NOT NULL,
    layer_id integer NOT NULL,
    disp_order integer,
    post_flag character(1) COLLATE pg_catalog."default",
    CONSTRAINT theme_layer_pkc PRIMARY KEY (theme_id, layer_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.theme_layer
    OWNER to postgres;

COMMENT ON TABLE public.theme_layer
    IS 'テーマ_レイヤ関連テーブル';

COMMENT ON COLUMN public.theme_layer.theme_id
    IS 'テーマID';

COMMENT ON COLUMN public.theme_layer.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.theme_layer.disp_order
    IS '表示順';

COMMENT ON COLUMN public.theme_layer.post_flag
    IS '投稿機能有効有無 1:有効 0:無効';