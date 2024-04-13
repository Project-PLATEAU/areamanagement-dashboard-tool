-- Table: public.region_summary

-- DROP TABLE IF EXISTS public.region_summary;

CREATE TABLE IF NOT EXISTS public.region_summary
(
    id integer NOT NULL DEFAULT nextval('region_summary_id_seq'::regclass),
    "カテゴリ" text COLLATE pg_catalog."default" NOT NULL,
    "和暦" text COLLATE pg_catalog."default" NOT NULL,
    "西暦" integer NOT NULL,
    "値" double precision,
    CONSTRAINT region_summary_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.region_summary
    OWNER to postgres;