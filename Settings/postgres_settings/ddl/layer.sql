-- Table: public.layer

-- DROP TABLE IF EXISTS public.layer;

CREATE TABLE IF NOT EXISTS public.layer
(
    layer_id integer NOT NULL,
    layer_type integer,
    layer_name character varying(100) COLLATE pg_catalog."default",
    layer_settings text COLLATE pg_catalog."default",
    icon_path text COLLATE pg_catalog."default",
    placeholder_flag character(1) COLLATE pg_catalog."default",
    CONSTRAINT layer_pkc PRIMARY KEY (layer_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.layer
    OWNER to postgres;

COMMENT ON TABLE public.layer
    IS 'レイヤのマスタテーブル.';

COMMENT ON COLUMN public.layer.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.layer.layer_type
    IS 'レイヤ種別 0:一般レイヤ,1: 投稿レイヤ,2:エリマネ・イベント活動レイヤ';

COMMENT ON COLUMN public.layer.layer_name
    IS 'レイヤ名称';

COMMENT ON COLUMN public.layer.layer_settings
    IS 'レイヤ設定:レイヤ定義をJSON形式で保持.JSONのフォーマットはterriaJSの仕様に従う.';

COMMENT ON COLUMN public.layer.icon_path
    IS 'アイコン画像パス:投稿レイヤまたはエリマネ・イベント活動レイヤのみ.アイコン画像の格納パス.';

COMMENT ON COLUMN public.layer.placeholder_flag
    IS 'layer_settingsにプレースホルダを使用している場合:1 ,使用していない場合:0';