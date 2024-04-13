package view3d.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.Activity;

@Transactional
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

	@Query(value = "SELECT activity_id,geom,insert_time,update_time,start_date_and_time,activity_type,group_type,activity_name,activity_place,activity_content,participants_count,remarks,parent_activity_id,end_date_and_time,post_user_id,publish_flag FROM activity WHERE parent_activity_id = :parentActivityId AND activity_type = :activityType ORDER BY start_date_and_time DESC , activity_id DESC ", nativeQuery = true)
	List<Activity> findByParentActivityIdAndActivityType(@Param("parentActivityId") Integer parentActivityId,@Param("activityType") Integer activityType);
	
	@Query(value = "SELECT activity_id,geom,insert_time,update_time,start_date_and_time,activity_type,group_type,activity_name,activity_place,activity_content,participants_count,remarks,parent_activity_id,end_date_and_time,post_user_id,publish_flag FROM activity WHERE activity_id = :activityId LIMIT 1 ", nativeQuery = true)
	Optional<Activity> findByActivityId(@Param("activityId") Integer activityId);

	@Query(value = "SELECT activity_id,geom,insert_time,update_time,start_date_and_time,activity_type,group_type,activity_name,activity_place,activity_content,participants_count,remarks,parent_activity_id,end_date_and_time,post_user_id,publish_flag FROM activity WHERE parent_activity_id = :parentActivityId AND activity_id <> :currentActivityId ORDER BY start_date_and_time,activity_id", nativeQuery = true)
	List<Activity> findByNotCurrentActivityIdParentActivityId(@Param("currentActivityId") Integer currentActivityId,@Param("parentActivityId") Integer parentActivityId);
	
	@Modifying
	@Query(value="DELETE FROM activity WHERE activity_id = :activityId",nativeQuery=true)
	int deleteByActivityId(@Param("activityId") Integer activityId);
	
}
