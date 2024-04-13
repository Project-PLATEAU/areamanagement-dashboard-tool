-- Table: public.login_user

-- DROP TABLE IF EXISTS public.login_user;

CREATE TABLE IF NOT EXISTS public.login_user
(
    user_id integer NOT NULL DEFAULT nextval('login_user_user_id_seq'::regclass),
    login_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    role character varying(10) COLLATE pg_catalog."default" NOT NULL,
    user_name character varying(100) COLLATE pg_catalog."default",
    mail_address character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT login_user_pkc PRIMARY KEY (user_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.login_user
    OWNER to postgres;

COMMENT ON TABLE public.login_user
    IS 'ログインユーザのマスタテーブル';

COMMENT ON COLUMN public.login_user.user_id
    IS 'ユーザID';

COMMENT ON COLUMN public.login_user.login_id
    IS 'ログインID';

COMMENT ON COLUMN public.login_user.password
    IS 'パスワード';

COMMENT ON COLUMN public.login_user.role
    IS '権限 システム管理者:"admin" ,エリマネ管理者:"erimane",地域住民ユーザ:"user"';