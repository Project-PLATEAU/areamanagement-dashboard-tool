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
), 
--parent_activity_idでグループ化して最新開始日時の投稿を取得
search_activity as (
    select
        parent_activity_id,
        Max(start_date_and_time) as start_date_and_time,
        COALESCE(SUM(participants_count), 0) AS participants_count
    from
        activity FULL
        OUTER JOIN login_user ON 1 = 1
    WHERE
        CASE
            --管理者 or erimaneは閲覧制限なし
            WHEN login_user.role = 'admin' OR login_user.role = 'erimane' THEN activity.activity_type = 2 
            --userの場合自身の投稿or公開フラグが1のもの
            WHEN login_user.role = 'user' THEN activity.activity_type = 2
            AND (
                activity.publish_flag = '1'
                OR activity.post_user_id = login_user.user_id
            ) 
            --それ以外の未ログインユーザは公開フラグが1のもの
            ELSE activity.activity_type = 2 AND activity.publish_flag = '1'
        END
    group by
        parent_activity_id,
        activity_type
) 
--同一の開始日時が複数ある場合の考慮（基本想定無し）
select
    MAX(activity.activity_id) AS activity_id,
    activity.parent_activity_id,
    activity_type,
    search_activity.participants_count AS participants_count,
    CASE
        WHEN ST_Z(ST_TransForm(activity.geom, 4326)) IS NULL THEN 100
        ELSE ST_Z(ST_TransForm(activity.geom, 4326))
    END as height,
    CASE
        WHEN ST_Z(ST_TransForm(activity.geom, 4326)) IS NULL THEN ST_Force3D(activity.geom, 100)
        ELSE geom
    END as geom,
    max(activity.activity_name) as activity_name
from
    activity
    inner join search_activity on search_activity.parent_activity_id = activity.parent_activity_id
    and search_activity.start_date_and_time = activity.start_date_and_time
    left outer join activity_type on activity.activity_type = activity_type.id
    left outer join group_type on activity.group_type = group_type.id FULL
    OUTER JOIN login_user ON 1 = 1
WHERE
    CASE
        --管理者 or erimaneは閲覧制限なし
        WHEN login_user.role = 'admin' OR login_user.role = 'erimane' THEN activity.activity_type = 2 
        --userの場合自身の投稿or公開フラグが1のもの
        WHEN login_user.role = 'user' THEN activity.activity_type = 2
        AND (
            activity.publish_flag = '1'
            OR activity.post_user_id = login_user.user_id
        ) 
        --それ以外の未ログインユーザは公開フラグが1のもの
        ELSE activity.activity_type = 2 AND activity.publish_flag = '1'
    END
group by
    activity.parent_activity_id,
    activity.activity_type,
    activity.geom,
    search_activity.participants_count
order by
    activity.parent_activity_id