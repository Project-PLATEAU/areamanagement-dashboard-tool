-- Table: public.kaiyu_jinryu_ninzuu_1

-- DROP TABLE IF EXISTS public.kaiyu_jinryu_ninzuu_1;

CREATE TABLE IF NOT EXISTS public.kaiyu_jinryu_ninzuu_1
(
    id integer NOT NULL,
    "日付" date,
    "利用者数" integer,
    "回数" integer,
    CONSTRAINT kaiyu_jinryu_ninzuu_1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.kaiyu_jinryu_ninzuu_1
    OWNER to postgres;

COMMENT ON TABLE public.kaiyu_jinryu_ninzuu_1
    IS '回遊人流_人数1';