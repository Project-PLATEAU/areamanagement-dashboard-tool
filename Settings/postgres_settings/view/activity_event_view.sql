-- View: public.activity_view_for_erimane

-- DROP VIEW public.activity_view_for_erimane;

CREATE OR REPLACE VIEW public.activity_view_for_erimane
 AS
 WITH attachment_file_names AS (
         SELECT attachments.activity_id,
            array_agg(attachments.attachment_file_name) AS file_names
           FROM attachments
          GROUP BY attachments.activity_id
        )
 SELECT activity.activity_id,
    activity.geom,
    activity.insert_time,
    activity.update_time,
    activity.start_date_and_time,
    activity.activity_type,
    activity_type.type_name AS activity_type_name,
    activity.group_type,
    activity.activity_name,
    activity.activity_place,
    activity.activity_content,
    activity.participants_count,
    activity.remarks,
    activity.parent_activity_id,
    activity.end_date_and_time,
    activity.post_user_id,
    activity.publish_flag,
    group_type.type_name AS group_type_name,
        CASE
            WHEN attachment_file_names.file_names[1] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[1]
        END AS "ファイル1",
        CASE
            WHEN attachment_file_names.file_names[2] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[2]
        END AS "ファイル2",
        CASE
            WHEN attachment_file_names.file_names[3] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[3]
        END AS "ファイル3",
        CASE
            WHEN attachment_file_names.file_names[4] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[4]
        END AS "ファイル4"
   FROM activity
     LEFT JOIN group_type ON activity.group_type = group_type.id
     LEFT JOIN activity_type ON activity.activity_type = activity_type.id
     LEFT JOIN attachment_file_names ON activity.activity_id = attachment_file_names.activity_id
  WHERE activity.activity_type = 1;

ALTER TABLE public.activity_view_for_erimane
    OWNER TO postgres;



-- View: public.activity_view_for_event

-- DROP VIEW public.activity_view_for_event;

CREATE OR REPLACE VIEW public.activity_view_for_event
 AS
 WITH attachment_file_names AS (
         SELECT attachments.activity_id,
            array_agg(attachments.attachment_file_name) AS file_names
           FROM attachments
          GROUP BY attachments.activity_id
        )
 SELECT activity.activity_id,
    activity.geom,
    activity.insert_time,
    activity.update_time,
    activity.start_date_and_time,
    activity.activity_type,
    activity_type.type_name AS activity_type_name,
    activity.group_type,
    activity.activity_name,
    activity.activity_place,
    activity.activity_content,
    activity.participants_count,
    activity.remarks,
    activity.parent_activity_id,
    activity.end_date_and_time,
    activity.post_user_id,
    activity.publish_flag,
    group_type.type_name AS group_type_name,
        CASE
            WHEN attachment_file_names.file_names[1] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[1]
        END AS "ファイル1",
        CASE
            WHEN attachment_file_names.file_names[2] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[2]
        END AS "ファイル2",
        CASE
            WHEN attachment_file_names.file_names[3] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[3]
        END AS "ファイル3",
        CASE
            WHEN attachment_file_names.file_names[4] IS NULL THEN ''::text
            ELSE attachment_file_names.file_names[4]
        END AS "ファイル4"
   FROM activity
     LEFT JOIN group_type ON activity.group_type = group_type.id
     LEFT JOIN activity_type ON activity.activity_type = activity_type.id
     LEFT JOIN attachment_file_names ON activity.activity_id = attachment_file_names.activity_id
  WHERE activity.activity_type = 2;

ALTER TABLE public.activity_view_for_event
    OWNER TO postgres;

