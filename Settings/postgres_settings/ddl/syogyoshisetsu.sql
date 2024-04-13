-- Table: public.syogyoshisetsu

-- DROP TABLE IF EXISTS public.syogyoshisetsu;

CREATE TABLE IF NOT EXISTS public.syogyoshisetsu
(
    id integer NOT NULL,
    "店舗名" character(100) COLLATE pg_catalog."default",
    "住所" character(100) COLLATE pg_catalog."default",
    "開設年" integer,
    "店舗面積" integer,
    CONSTRAINT syogyoshisetsu_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.syogyoshisetsu
    OWNER to postgres;

COMMENT ON TABLE public.syogyoshisetsu
    IS '商業施設';