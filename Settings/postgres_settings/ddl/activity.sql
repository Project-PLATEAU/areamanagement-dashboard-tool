-- Table: public.activity

-- DROP TABLE IF EXISTS public.activity;

CREATE TABLE IF NOT EXISTS public.activity
(
    activity_id integer NOT NULL,
    parent_activity_id integer,
    geom geometry(PointZ,3857) NOT NULL,
    start_date_and_time timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    end_date_and_time timestamp(6) without time zone,
    activity_type integer NOT NULL,
    group_type integer NOT NULL,
    activity_name text COLLATE pg_catalog."default",
    activity_place text COLLATE pg_catalog."default",
    activity_content text COLLATE pg_catalog."default",
    participants_count integer,
    remarks text COLLATE pg_catalog."default",
    post_user_id integer,
    publish_flag character(1) COLLATE pg_catalog."default",
    insert_time timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT activity_pkey PRIMARY KEY (activity_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.activity
    OWNER to postgres;

COMMENT ON TABLE public.activity
    IS 'エリマネ・イベント活動';

COMMENT ON COLUMN public.activity.activity_id
    IS '活動ID';

COMMENT ON COLUMN public.activity.parent_activity_id
    IS '親の活動ID';

COMMENT ON COLUMN public.activity.geom
    IS 'Geometry';

COMMENT ON COLUMN public.activity.start_date_and_time
    IS '開始日時';

COMMENT ON COLUMN public.activity.end_date_and_time
    IS '終了日時';

COMMENT ON COLUMN public.activity.activity_type
    IS '地域活動種別ID';

COMMENT ON COLUMN public.activity.group_type
    IS 'エリアマネジメント団体種別ID';

COMMENT ON COLUMN public.activity.activity_name
    IS '活動名';

COMMENT ON COLUMN public.activity.activity_place
    IS '活動場所';

COMMENT ON COLUMN public.activity.activity_content
    IS '活動内容';

COMMENT ON COLUMN public.activity.participants_count
    IS '参加者数';

COMMENT ON COLUMN public.activity.remarks
    IS '備考';

COMMENT ON COLUMN public.activity.post_user_id
    IS '投稿ユーザID';

COMMENT ON COLUMN public.activity.publish_flag
    IS '公開フラグ 1:公開 0:非公開';

COMMENT ON COLUMN public.activity.insert_time
    IS '作成日時';

COMMENT ON COLUMN public.activity.update_time
    IS '更新日時';