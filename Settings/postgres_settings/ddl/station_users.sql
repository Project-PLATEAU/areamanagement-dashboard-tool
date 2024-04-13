-- Table: public.station_users

-- DROP TABLE IF EXISTS public.station_users;

CREATE TABLE IF NOT EXISTS public.station_users
(
    "会社名" character(50) COLLATE pg_catalog."default",
    "年" character(10) COLLATE pg_catalog."default",
    "利用者数" integer,
    id integer NOT NULL DEFAULT nextval('station_users_id_seq'::regclass),
    CONSTRAINT station_users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.station_users
    OWNER to postgres;

COMMENT ON TABLE public.station_users
    IS '駅の乗降客数';