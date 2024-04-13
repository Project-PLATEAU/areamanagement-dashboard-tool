-- Table: public.group_type

-- DROP TABLE IF EXISTS public.group_type;

CREATE TABLE IF NOT EXISTS public.group_type
(
    id integer NOT NULL DEFAULT nextval('group_type_id_seq'::regclass),
    type_name text COLLATE pg_catalog."default",
    CONSTRAINT group_type_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.group_type
    OWNER to postgres;

COMMENT ON TABLE public.group_type
    IS 'エリアマネジメント団体種別';

COMMENT ON COLUMN public.group_type.id
    IS 'id';

COMMENT ON COLUMN public.group_type.type_name
    IS 'エリアマネジメント団体種別名';