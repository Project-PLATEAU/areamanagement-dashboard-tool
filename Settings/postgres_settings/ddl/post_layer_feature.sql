-- Table: public.post_layer_feature

-- DROP TABLE IF EXISTS public.post_layer_feature;

CREATE TABLE IF NOT EXISTS public.post_layer_feature
(
    feature_id integer NOT NULL,
    layer_id integer NOT NULL,
    publish_flag character(1) COLLATE pg_catalog."default",
    geometry geometry(PointZ,3857),
    post_user_id integer,
    post_datetime timestamp without time zone,
    parent_feature_id integer,
    item_1 text COLLATE pg_catalog."default",
    item_2 text COLLATE pg_catalog."default",
    item_3 text COLLATE pg_catalog."default",
    item_4 text COLLATE pg_catalog."default",
    item_5 text COLLATE pg_catalog."default",
    item_6 text COLLATE pg_catalog."default",
    item_7 text COLLATE pg_catalog."default",
    item_8 text COLLATE pg_catalog."default",
    item_9 text COLLATE pg_catalog."default",
    item_10 text COLLATE pg_catalog."default",
    CONSTRAINT post_layer_feature_pkc PRIMARY KEY (feature_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.post_layer_feature
    OWNER to postgres;

COMMENT ON TABLE public.post_layer_feature
    IS '投稿レイヤフィーチャテーブル';

COMMENT ON COLUMN public.post_layer_feature.feature_id
    IS 'フィーチャID';

COMMENT ON COLUMN public.post_layer_feature.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.post_layer_feature.publish_flag
    IS '公開フラグ:1:公開 0:非公開';

COMMENT ON COLUMN public.post_layer_feature.geometry
    IS 'ジオメトリ:データタイプ：ポイント';

COMMENT ON COLUMN public.post_layer_feature.post_user_id
    IS '投稿ユーザID';

COMMENT ON COLUMN public.post_layer_feature.post_datetime
    IS '投稿日時';

COMMENT ON COLUMN public.post_layer_feature.parent_feature_id
    IS '親フィーチャID';

COMMENT ON COLUMN public.post_layer_feature.item_1
    IS '項目1:項目ID=1の値';

COMMENT ON COLUMN public.post_layer_feature.item_2
    IS '項目2:項目ID=2の値';

COMMENT ON COLUMN public.post_layer_feature.item_3
    IS '項目3:項目ID=3の値';

COMMENT ON COLUMN public.post_layer_feature.item_4
    IS '項目4:項目ID=4の値';

COMMENT ON COLUMN public.post_layer_feature.item_5
    IS '項目5:項目ID=5の値';

COMMENT ON COLUMN public.post_layer_feature.item_6
    IS '項目6:項目ID=6の値';

COMMENT ON COLUMN public.post_layer_feature.item_7
    IS '項目7:項目ID=7の値';

COMMENT ON COLUMN public.post_layer_feature.item_8
    IS '項目8:項目ID=8の値';

COMMENT ON COLUMN public.post_layer_feature.item_9
    IS '項目9:項目ID=9の値';

COMMENT ON COLUMN public.post_layer_feature.item_10
    IS '項目10:項目ID=10の値';