--コストカラム追加SQL
--歩行空間ネットワークデータ（リンク）取込後に本SQLを実行してください。
ALTER TABLE link_3d ALTER COLUMN distance TYPE double precision;
ALTER TABLE link_3d ADD COLUMN cost_wheelchair double precision;
ALTER TABLE link_3d ADD COLUMN cost_elderly double precision;
ALTER TABLE link_3d ADD COLUMN cost_brail double precision;

COMMENT ON COLUMN public.link_3d.cost_wheelchair
    IS '車いす利用者向け経路探索コスト';
COMMENT ON COLUMN public.link_3d.cost_elderly
    IS '高齢者向け経路探索コスト';
COMMENT ON COLUMN public.link_3d.cost_brail
    IS '視覚障害者向け経路探索コスト';