-- Table: public.theme_graph_list

-- DROP TABLE IF EXISTS public.theme_graph_list;

CREATE TABLE IF NOT EXISTS public.theme_graph_list
(
    theme_id integer NOT NULL,
    graph_id integer NOT NULL,
    top_left_x integer,
    top_left_y integer,
    panel_width integer,
    panel_height integer,
    CONSTRAINT theme_graph_list_pkc PRIMARY KEY (theme_id, graph_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.theme_graph_list
    OWNER to postgres;

COMMENT ON TABLE public.theme_graph_list
    IS 'テーマ_グラフ・リスト関連テーブル';

COMMENT ON COLUMN public.theme_graph_list.theme_id
    IS 'テーマID';

COMMENT ON COLUMN public.theme_graph_list.graph_id
    IS 'グラフ・リストID';

COMMENT ON COLUMN public.theme_graph_list.top_left_x
    IS '画面上のX座標:グリッド数で指定(dashboard側:1~3、viewer側:4~7)';

COMMENT ON COLUMN public.theme_graph_list.top_left_y
    IS '画面上のY座標:グリッド数で指定';

COMMENT ON COLUMN public.theme_graph_list.panel_width
    IS 'パネル幅:グリッド数で指定(dashboard側:1~4、viewer側:1~8)';

COMMENT ON COLUMN public.theme_graph_list.panel_height
    IS 'パネル高さ:グリッド数で指定';