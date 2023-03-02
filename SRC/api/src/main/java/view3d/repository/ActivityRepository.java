package view3d.repository;

import java.time.LocalDateTime;
import java.util.List;

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

	@Query(value = "SELECT * FROM activity WHERE parent_activity_id = :parentActivityId AND activity_type = :activityType ORDER BY start_date_and_time DESC , activity_id DESC ", nativeQuery = true)
	List<Activity> findByParentActivityIdAndActivityType(Integer parentActivityId,Integer activityType);
	
	@Query(value = "SELECT * FROM activity WHERE parent_activity_id = :parentActivityId AND activity_id <> :currentActivityId ORDER BY start_date_and_time,activity_id", nativeQuery = true)
	List<Activity> findByNotCurrentActivityIdParentActivityId(Integer currentActivityId,Integer parentActivityId);
	
	@Modifying
	@Query(value="DELETE FROM activity WHERE activity_id = :activityId",nativeQuery=true)
	int deleteByActivityId(@Param("activityId") Integer activityId);
	
}
