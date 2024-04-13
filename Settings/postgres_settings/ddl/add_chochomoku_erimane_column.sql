--町名を追加
ALTER TABLE chochomokukai_erimane ADD town_name character varying(96);
COMMENT ON COLUMN chochomokukai_erimane.town_name IS '町名';

--丁名を追加
ALTER TABLE chochomokukai_erimane ADD block_name character varying(96);
COMMENT ON COLUMN chochomokukai_erimane.block_name IS '丁名';

--エリアマネジメント団体種別名を追加
ALTER TABLE chochomokukai_erimane ADD area_management_type character varying(96);
COMMENT ON COLUMN chochomokukai_erimane.area_management_type IS 'エリアマネジメント団体種別名';