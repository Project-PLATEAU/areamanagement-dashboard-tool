-- Table: public.chika2

-- DROP TABLE IF EXISTS public.chika2;

CREATE TABLE IF NOT EXISTS public.chika2
(
    id integer NOT NULL,
    "地点名" character(50) COLLATE pg_catalog."default",
    "和暦" character(20) COLLATE pg_catalog."default",
    "西暦" integer,
    "地価" integer,
    "区分" character(20) COLLATE pg_catalog."default",
    "エリア" character(20) COLLATE pg_catalog."default",
    CONSTRAINT chika2_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.chika2
    OWNER to postgres;