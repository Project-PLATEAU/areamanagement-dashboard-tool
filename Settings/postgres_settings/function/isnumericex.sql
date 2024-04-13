-- FUNCTION: public.isnumericex(character varying)

-- DROP FUNCTION IF EXISTS public.isnumericex(character varying);

CREATE OR REPLACE FUNCTION public.isnumericex(
	c_num character varying)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
 
	n_result 		numeric;  -- 一時変数
	
BEGIN

    --数値へcastする
	select cast(c_num as numeric) into n_result;

	--castできる時はtrueでリターン
	return TRUE;
	
    --castできずエラーになる時はここのEXCEPTIONに入る
	EXCEPTION
	WHEN OTHERS THEN
	--エラー時はfalseでリターン
	return FALSE;
 
END;
$BODY$;

ALTER FUNCTION public.isnumericex(character varying)
    OWNER TO postgres;
