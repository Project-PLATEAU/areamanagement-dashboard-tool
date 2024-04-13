-- Table: public.layer_graph_cooporation

-- DROP TABLE IF EXISTS public.layer_graph_cooporation;

CREATE TABLE IF NOT EXISTS public.layer_graph_cooporation
(
    cooperation_id integer NOT NULL,
    cooperation_type integer,
    cooperation_option text COLLATE pg_catalog."default",
    layer_id integer NOT NULL,
    graph_id integer NOT NULL,
    CONSTRAINT layer_graph_cooporation_pkc PRIMARY KEY (cooperation_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.layer_graph_cooporation
    OWNER to postgres;

COMMENT ON TABLE public.layer_graph_cooporation
    IS 'レイヤ_グラフ連携テーブル';

COMMENT ON COLUMN public.layer_graph_cooporation.cooperation_id
    IS '連携ID';

COMMENT ON COLUMN public.layer_graph_cooporation.cooperation_type
    IS '連携種別:
0: グラフリスト->3D都市モデルビューワ（フォーカス)
1: 3D都市モデルビューワ->リスト（データ絞込）
2: シングルリスト->3D都市モデルビューワ（レイヤ切替）※絞り込まれる側のダッシュボードのグラフ・リストIDをレイヤIDにセット
3: シングルリスト->リスト（データ絞込）
4: 円グラフ->3D建物モデル（style切り替え）
';

COMMENT ON COLUMN public.layer_graph_cooporation.cooperation_option
    IS '連携オプション:連携オプションをJSON形式で定義';

COMMENT ON COLUMN public.layer_graph_cooporation.layer_id
    IS 'レイヤID';

COMMENT ON COLUMN public.layer_graph_cooporation.graph_id
    IS 'グラフ・リストID';