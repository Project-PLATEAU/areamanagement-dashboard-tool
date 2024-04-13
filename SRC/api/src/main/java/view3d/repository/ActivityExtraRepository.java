package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.ActivityExtra;

@Transactional
@Repository
public interface ActivityExtraRepository extends JpaRepository<ActivityExtra, Integer> {
	
	@Query(value = "SELECT activity_id,ST_X(ST_TransForm(activity.geom, 4326)) as longitude,ST_Y(ST_TransForm(activity.geom, 4326)) as latitude,insert_time,update_time,start_date_and_time,activity_type,group_type,activity_name,activity_place,activity_content,participants_count,remarks,parent_activity_id,end_date_and_time,post_user_id,publish_flag FROM activity WHERE activity_type = :activityType ORDER BY start_date_and_time DESC ", nativeQuery = true)
	List<ActivityExtra> findActivityType(@Param("activityType") Integer activityType);
	
}
