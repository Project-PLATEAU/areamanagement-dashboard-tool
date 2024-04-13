-- Table: public.erimane_ninchido

-- DROP TABLE IF EXISTS public.erimane_ninchido;

CREATE TABLE IF NOT EXISTS public.erimane_ninchido
(
    id integer NOT NULL,
    "エリア" character(50) COLLATE pg_catalog."default",
    "和暦" character(10) COLLATE pg_catalog."default",
    "西暦" integer,
    "認知度" numeric,
    CONSTRAINT erimane_ninchido_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.erimane_ninchido
    OWNER to postgres;

COMMENT ON TABLE public.erimane_ninchido
    IS 'エリマネ認知度';