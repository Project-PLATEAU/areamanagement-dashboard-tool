-- Table: public.theme

-- DROP TABLE IF EXISTS public.theme;

CREATE TABLE IF NOT EXISTS public.theme
(
    theme_id integer NOT NULL,
    theme_name character varying(100) COLLATE pg_catalog."default",
    theme_group_name character varying(100) COLLATE pg_catalog."default",
    disp_order integer,
    publish_flag character(1) COLLATE pg_catalog."default",
    post_flag character(1) COLLATE pg_catalog."default",
    information_text text COLLATE pg_catalog."default",
    switch_flag character(1) COLLATE pg_catalog."default",
    switch_query text COLLATE pg_catalog."default",
    switch_item_name_column_name character varying(256) COLLATE pg_catalog."default",
    switch_item_value_column_name character varying(256) COLLATE pg_catalog."default",
    switch_placeholder_name character varying(256) COLLATE pg_catalog."default",
    switch_placeholder_default_value character varying(256) COLLATE pg_catalog."default",
    CONSTRAINT theme_pkc PRIMARY KEY (theme_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.theme
    OWNER to postgres;

COMMENT ON TABLE public.theme
    IS 'テーマのマスタテーブル';

COMMENT ON COLUMN public.theme.theme_id
    IS 'テーマID';

COMMENT ON COLUMN public.theme.theme_name
    IS 'テーマ名称';

COMMENT ON COLUMN public.theme.theme_group_name
    IS 'テーマグループ名称';

COMMENT ON COLUMN public.theme.disp_order
    IS '表示順';

COMMENT ON COLUMN public.theme.publish_flag
    IS '公開フラグ:1:公開 0:非公開';

COMMENT ON COLUMN public.theme.post_flag
    IS '投稿機能有効フラグ:1:有効 0:無効';

COMMENT ON COLUMN public.theme.information_text
    IS '案内文言';

COMMENT ON COLUMN public.theme.switch_flag
    IS 'テーマ内切替フラグ 1:有効 0:無効';

COMMENT ON COLUMN public.theme.switch_query
    IS 'テーマ内切替項目取得SQL';

COMMENT ON COLUMN public.theme.switch_item_name_column_name
    IS '切替項目名 カラム名(※セレクトボックスの表示名に該当)';

COMMENT ON COLUMN public.theme.switch_item_value_column_name
    IS '切替項目値カラム名(※セレクトボックスの値に該当)';

COMMENT ON COLUMN public.theme.switch_placeholder_name
    IS '切替項目プレースホルダ名 (※graph_listテーブルのquery_text、layerテーブルのlayer_settingsでの置換対象値)';

COMMENT ON COLUMN public.theme.switch_placeholder_default_value
    IS '切替項目プレースホルダデフォルト値';