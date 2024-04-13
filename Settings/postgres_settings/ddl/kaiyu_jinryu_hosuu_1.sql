-- Table: public.kaiyu_jinryu_hosuu_1

-- DROP TABLE IF EXISTS public.kaiyu_jinryu_hosuu_1;

CREATE TABLE IF NOT EXISTS public.kaiyu_jinryu_hosuu_1
(
    id integer NOT NULL,
    "日付" date,
    "歩数" integer,
    "天気" character(20) COLLATE pg_catalog."default",
    "最低気温" double precision,
    "最高気温" double precision,
    "回数" integer,
    CONSTRAINT kaiyu_jinryu_hosuu_1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.kaiyu_jinryu_hosuu_1
    OWNER to postgres;

COMMENT ON TABLE public.kaiyu_jinryu_hosuu_1
    IS '回遊人流_歩数1';