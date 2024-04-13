-- Table: public.activity_type

-- DROP TABLE IF EXISTS public.activity_type;

CREATE TABLE IF NOT EXISTS public.activity_type
(
    id integer NOT NULL DEFAULT nextval('activity_type_id_seq'::regclass),
    type_name text COLLATE pg_catalog."default",
    CONSTRAINT activity_type_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.activity_type
    OWNER to postgres;

COMMENT ON TABLE public.activity_type
    IS '地域活動種別';

COMMENT ON COLUMN public.activity_type.id
    IS 'id';

COMMENT ON COLUMN public.activity_type.type_name
    IS '地域活動種別名';