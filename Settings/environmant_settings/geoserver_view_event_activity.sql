--イベント活動viewSQL
--編集は不要です。
with search_activity as (
    select
            parent_activity_id,
            Max(start_date_and_time) as start_date_and_time
        from
            activity
        group by
            parent_activity_id,
            activity_type
)

select
    activity.activity_id,
    activity.parent_activity_id,
    activity.geom,
    activity_type.type_name as "地域活動種別",
    group_type.type_name as "エリアマネジメント団体",
    activity.activity_name as "活動名",
    activity.activity_place as "活動場所",
    activity.activity_content as "活動内容",
    activity.participants_count as "参加者数",
    to_char(
        activity.start_date_and_time,
        'YYYY/MM/DD HH24:MI'
    ) as "開始日時",
    to_char(activity.end_date_and_time, 'YYYY/MM/DD HH24:MI') as "終了日時",
    activity.remarks as "備考",
    activity.activity_type
from
    activity
    left outer join activity_type on activity.activity_type = activity_type.id
    left outer join group_type on activity.group_type = group_type.id
    inner join search_activity on 
    search_activity.parent_activity_id = activity.parent_activity_id
    and search_activity.start_date_and_time = activity.start_date_and_time
where
    activity.activity_type = 2
