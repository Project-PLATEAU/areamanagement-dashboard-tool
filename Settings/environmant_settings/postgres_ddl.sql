
-- ■■■エリマネ・イベント活動テーブル作成■■■

DROP SEQUENCE IF EXISTS seq_activity_id;
CREATE SEQUENCE seq_activity_id
    INCREMENT 1
    START 1;

DROP TABLE IF EXISTS public.activity;
CREATE TABLE IF NOT EXISTS public.activity
(
    activity_id integer NOT NULL,
    parent_activity_id integer,
    geom geometry(Point,3857) NOT NULL,
    insert_time timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    start_date_and_time timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    end_date_and_time timestamp(6) without time zone,
    activity_type integer NOT NULL,
    group_type integer NOT NULL,
    activity_name text COLLATE pg_catalog."default",
    activity_place text COLLATE pg_catalog."default",
    activity_content text COLLATE pg_catalog."default",
    participants_count integer,
    remarks text COLLATE pg_catalog."default",
    CONSTRAINT activity_pkey PRIMARY KEY (activity_id)
);

COMMENT ON TABLE public.activity
    IS 'エリマネ・イベント活動';

COMMENT ON COLUMN public.activity.activity_id
    IS '活動ID';
    
COMMENT ON COLUMN public.activity.parent_activity_id
    IS '親の活動ID';

COMMENT ON COLUMN public.activity.geom
    IS 'Geometry';

COMMENT ON COLUMN public.activity.insert_time
    IS '作成日時';

COMMENT ON COLUMN public.activity.update_time
    IS '更新日時';

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

-- ■■■エリマネ・イベント活動 地域活動種別テーブル作成■■■

DROP TABLE IF EXISTS public.activity_type;

CREATE TABLE IF NOT EXISTS public.activity_type
(
    id integer NOT NULL,
    type_name text COLLATE pg_catalog."default",
    CONSTRAINT activity_type_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE public.activity_type
    IS 'エリマネ・イベント活動 地域活動種別';

COMMENT ON COLUMN public.activity_type.id
    IS 'id';

COMMENT ON COLUMN public.activity_type.type_name
    IS '地域活動種別名';

-- フロント側の各layerの再描画、geoserver側での取得条件で使用しているためidは固定（名称変更可）
INSERT INTO public.activity_type(
	id, type_name)
	VALUES (1, 'エリマネ活動'),(2, 'イベント活動');

-- ■■■エリマネ・イベント活動 エリアマネジメント団体種別テーブル作成■■■

DROP SEQUENCE IF EXISTS group_type_id_seq;
CREATE SEQUENCE group_type_id_seq
    INCREMENT 1
    START 1;

DROP TABLE IF EXISTS public.group_type;

CREATE TABLE IF NOT EXISTS public.group_type
(
    id integer NOT NULL DEFAULT nextval('group_type_id_seq'::regclass),
    type_name text COLLATE pg_catalog."default",
    CONSTRAINT group_type_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE public.group_type
    IS 'エリマネ・イベント活動 エリアマネジメント団体種別';

COMMENT ON COLUMN public.group_type.id
    IS 'id';

COMMENT ON COLUMN public.group_type.type_name
    IS 'エリアマネジメント団体種別名';

-- ■■■エリマネ・イベント活動 添付ファイルテーブル作成■■■

DROP SEQUENCE IF EXISTS images_id_seq;
CREATE SEQUENCE images_id_seq
    INCREMENT 1
    START 1;

DROP TABLE IF EXISTS public.attachments;

CREATE TABLE IF NOT EXISTS public.attachments
(
    id integer NOT NULL DEFAULT nextval('images_id_seq'::regclass),
    activity_id integer NOT NULL,
    created timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    attachment_file_name text COLLATE pg_catalog."default",
    CONSTRAINT images_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE public.attachments
    IS 'エリマネ・イベント活動 添付ファイル';

COMMENT ON COLUMN public.attachments.id
    IS 'id';

COMMENT ON COLUMN public.attachments.activity_id
    IS '活動ID';

COMMENT ON COLUMN public.attachments.created
    IS '作成日時';

COMMENT ON COLUMN public.attachments.attachment_file_name
    IS '添付ファイル名';
    
-- ■■■ログインユーザーテーブル作成■■■

CREATE TABLE IF NOT EXISTS public.login_user
(
    user_id character varying(10) COLLATE pg_catalog."default" NOT NULL,
    login_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    role character varying(10) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT login_user_pkc PRIMARY KEY (user_id)
);
COMMENT ON TABLE public.login_user
    IS 'ログインユーザ';

COMMENT ON COLUMN public.login_user.user_id
    IS 'ユーザID';

COMMENT ON COLUMN public.login_user.login_id
    IS 'ログインID';

COMMENT ON COLUMN public.login_user.password
    IS 'パスワード';

COMMENT ON COLUMN public.login_user.role
    IS 'ロール: admin or user';

-- ■■■エリマネ活動ビュー作成■■■
DROP VIEW IF EXISTS public.v_area_management_activity;
CREATE VIEW public.v_area_management_activity AS 
 SELECT activity.activity_id,
    activity.activity_name
   FROM activity
  WHERE (activity.activity_type = 1);
-- ■■■イベント活動ビュー作成■■■
DROP VIEW IF EXISTS public.v_event_activity;
CREATE VIEW public.v_event_activity AS 
 SELECT activity.activity_id,
    activity.activity_name
   FROM activity
  WHERE (activity.activity_type = 2);