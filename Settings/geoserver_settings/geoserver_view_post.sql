--ユーザIDから権限情報取得
with login_user as (
    SELECT
        user_id,
        role
    FROM
        login_user
    WHERE
        user_id = CAST(%user_id% AS integer)
    LIMIT
        1
) 
--権限による条件分岐
SELECT
    feature_id,
    layer_id,
    parent_feature_id,
    CASE
        WHEN ST_Z(ST_TransForm(geometry, 4326)) IS NULL THEN 250
        ELSE ST_Z(ST_TransForm(geometry, 4326))
    END as height,
    CASE
        WHEN ST_Z(ST_TransForm(geometry, 4326)) IS NULL THEN ST_Force3D(geometry, 250)
        ELSE geometry
    END as geometry,
    CASE
        WHEN '%label_item%' = 'item_1' THEN item_1
        WHEN '%label_item%' = 'item_2' THEN item_2
        WHEN '%label_item%' = 'item_3' THEN item_3
        WHEN '%label_item%' = 'item_4' THEN item_4
        WHEN '%label_item%' = 'item_5' THEN item_5
        WHEN '%label_item%' = 'item_6' THEN item_6
        WHEN '%label_item%' = 'item_7' THEN item_7
        WHEN '%label_item%' = 'item_8' THEN item_8
        WHEN '%label_item%' = 'item_9' THEN item_9
        WHEN '%label_item%' = 'item_10' THEN item_10
        ELSE item_1
    END as item_name
FROM
    post_layer_feature FULL
    OUTER JOIN login_user ON 1 = 1
WHERE
    CASE
        --管理者 or erimaneは閲覧制限なし
        WHEN login_user.role = 'admin' OR login_user.role = 'erimane' THEN layer_id = CAST(%layer_id% AS integer) 
        --userの場合自身の投稿or公開フラグが1のもの
        WHEN login_user.role = 'user' THEN layer_id = CAST(%layer_id% AS integer)
        AND (
            post_layer_feature.publish_flag = '1'
            OR post_layer_feature.post_user_id = login_user.user_id
        ) 
        --それ以外の未ログインユーザは公開フラグが1のもの
        ELSE layer_id = CAST(%layer_id% AS integer) AND post_layer_feature.publish_flag = '1'
    END