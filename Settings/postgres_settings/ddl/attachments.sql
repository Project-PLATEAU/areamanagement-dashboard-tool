-- Table: public.attachments

-- DROP TABLE IF EXISTS public.attachments;

CREATE TABLE IF NOT EXISTS public.attachments
(
    id integer NOT NULL DEFAULT nextval('images_id_seq'::regclass),
    activity_id integer NOT NULL,
    created timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    attachment_file_name text COLLATE pg_catalog."default",
    CONSTRAINT images_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.attachments
    OWNER to postgres;

COMMENT ON TABLE public.attachments
    IS 'エリマネ・イベント活動_添付ファイル';

COMMENT ON COLUMN public.attachments.id
    IS 'id';

COMMENT ON COLUMN public.attachments.activity_id
    IS '活動ID';

COMMENT ON COLUMN public.attachments.created
    IS '作成日時';

COMMENT ON COLUMN public.attachments.attachment_file_name
    IS '添付ファイル名';