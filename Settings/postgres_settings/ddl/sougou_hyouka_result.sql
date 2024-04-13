-- Table: public.sougou_hyouka_result

-- DROP TABLE IF EXISTS public.sougou_hyouka_result;

CREATE TABLE IF NOT EXISTS public.sougou_hyouka_result
(
    id integer NOT NULL DEFAULT nextval('sougou_hyouka_result_id_seq'::regclass),
    "カテゴリ" text COLLATE pg_catalog."default",
    "年度" integer,
    "全体比較" double precision,
    "過去比較" double precision,
    CONSTRAINT sougou_hyouka_result_pk1 PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.sougou_hyouka_result
    OWNER to postgres;

COMMENT ON TABLE public.sougou_hyouka_result
    IS '総合評価結果';