-- Table: public.syokencyosa_shijiritsu

-- DROP TABLE IF EXISTS public.syokencyosa_shijiritsu;

CREATE TABLE IF NOT EXISTS public.syokencyosa_shijiritsu
(
    id integer NOT NULL,
    "商圏エリア" character(50) COLLATE pg_catalog."default",
    "割合" double precision,
    CONSTRAINT syokencyosa_shijiritsu_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.syokencyosa_shijiritsu
    OWNER to postgres;

COMMENT ON TABLE public.syokencyosa_shijiritsu
    IS '商圏調査の支持率';