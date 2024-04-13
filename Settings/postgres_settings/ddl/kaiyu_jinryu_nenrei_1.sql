-- Table: public.kaiyu_jinryu_nenrei_1

-- DROP TABLE IF EXISTS public.kaiyu_jinryu_nenrei_1;

CREATE TABLE IF NOT EXISTS public.kaiyu_jinryu_nenrei_1
(
    id integer NOT NULL,
    "項目" character(50) COLLATE pg_catalog."default",
    "人数" double precision,
    "割合" double precision,
    "回数" integer,
    "項目id" integer,
    CONSTRAINT kaiyu_jinryu_nenrei_1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.kaiyu_jinryu_nenrei_1
    OWNER to postgres;

COMMENT ON TABLE public.kaiyu_jinryu_nenrei_1
    IS '回遊人流_年齢1';