package jp.co.ajiko.urbandx.view3d.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "activity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "activity_id",updatable=false)
	private Integer activityId;
	
	@Column(name = "geom",updatable=false)
	private String geom;

	@Column(name = "insert_time",updatable=false)
	private LocalDateTime insertTime;

	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@Column(name = "start_date_and_time")
	private LocalDateTime startDateAndTime;
	
	@Column(name = "end_date_and_time")
	private LocalDateTime endDateAndTime;
	
	@Column(name = "activity_type")
	private Integer activityType;
	
	@Column(name = "group_type")
	private Integer groupType;

	@Column(name = "activity_name")
	private String activityName;
	
	@Column(name = "activity_place")
	private String activityPlace;
	
	@Column(name = "activity_content")
	private String activityContent;

	@Column(name = "participants_count")
	private Integer participantsCount;

	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "parent_activity_id")
	private Integer parentActivityId;
	
	@ManyToOne
    @JoinColumn(name = "activity_type", referencedColumnName = "id")
    @MapsId("activityType")
    private ActivityType activityTypeObj;
	
	@ManyToOne
    @JoinColumn(name = "group_type", referencedColumnName = "id")
    @MapsId("groupType")
    private GroupType groupTypeObj;
}
