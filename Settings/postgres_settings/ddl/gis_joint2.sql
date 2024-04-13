-- Table: public.gis_joint2

-- DROP TABLE IF EXISTS public.gis_joint2;

CREATE TABLE IF NOT EXISTS public.gis_joint2
(
    id integer NOT NULL,
    "地点名" character(50) COLLATE pg_catalog."default",
    "エリマネ" character(50) COLLATE pg_catalog."default",
    "カテゴリ" character(50) COLLATE pg_catalog."default",
    "和暦" character(10) COLLATE pg_catalog."default",
    "西暦" integer,
    "数" integer,
    CONSTRAINT gis_joint2_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.gis_joint2
    OWNER to postgres;

COMMENT ON TABLE public.gis_joint2
    IS 'GIS結合用';