package jp.co.ajiko.urbandx.view3d.repository.jdbc;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ajiko.urbandx.view3d.form.ActivityForm;

@Component
@Configuration
@ComponentScan
@EnableTransactionManagement
public class ActivityJdbc {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${app.activity.view.epsg}")
	private Integer viewEpsg;
	
	@Value("${app.activity.data.epsg}")
	private Integer dataEpsg;

	@Transactional
	public Integer insert(ActivityForm activityForm, String longLat) {
		jdbcTemplate.update("INSERT INTO activity " + "(activity_id, geom, "
				+ "insert_time, update_time, start_date_and_time,end_date_and_time,"
				+ "activity_type,group_type, activity_name, activity_place, activity_content,participants_count,remarks,parent_activity_id) "
				+ "VALUES "
				+ "(nextval('seq_activity_id'), ST_Transform(ST_GeomFromText('POINT('||?||')', ?), ?),"
				+ "CURRENT_TIMESTAMP, CURRENT_TIMESTAMP," + "?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?);"
				,
				longLat,viewEpsg,dataEpsg,activityForm.getStartDateAndTime(),activityForm.getEndDateAndTime(),activityForm.getActivityType(),activityForm.getGroupType(),activityForm.getActivityName(),activityForm.getActivityPlace(),activityForm.getActivityContent(),activityForm.getParticipantCount(),activityForm.getRemarks(),activityForm.getParentActivityId());
		return jdbcTemplate.queryForObject("SELECT lastval()", Integer.class);
	}
	
	@Transactional
	public Integer quoteInsert(ActivityForm activityForm) {
		jdbcTemplate.update("INSERT INTO activity " + "(activity_id, geom, "
				+ "insert_time, update_time, start_date_and_time,end_date_and_time,"
				+ "activity_type,group_type, activity_name, activity_place, activity_content,participants_count,remarks,parent_activity_id) "
				+ "VALUES "
				+ "(nextval('seq_activity_id'),ST_GeomFromEWKB(ST_AsEWKB(CAST(? AS TEXT))),"
				+ "CURRENT_TIMESTAMP, CURRENT_TIMESTAMP," + "?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?);"
				,
				activityForm.getGeom(),activityForm.getStartDateAndTime(),activityForm.getEndDateAndTime(),activityForm.getActivityType(),activityForm.getGroupType(),activityForm.getActivityName(),activityForm.getActivityPlace(),activityForm.getActivityContent(),activityForm.getParticipantCount(),activityForm.getRemarks(),activityForm.getParentActivityId());
		return jdbcTemplate.queryForObject("SELECT lastval()", Integer.class);
	}

}
