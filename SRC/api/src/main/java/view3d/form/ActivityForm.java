package view3d.form;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class ActivityForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "活動ID", example = "100")
	private Integer activityId;
	
	@ApiModelProperty(value = "Geometry文字列", example = "XXXXXXXXXXX")
	private String geom;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@ApiModelProperty(value = "開始日時", example = "2022-01-01T10:00")
	@NotNull
	private LocalDateTime startDateAndTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@ApiModelProperty(value = "終了日時", example = "2022-01-01T15:00")
	private LocalDateTime endDateAndTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@ApiModelProperty(value = "投稿日時", example = "2022-01-01T15:00")
	private LocalDateTime insertTime;
	
	@ApiModelProperty(value = "活動種別", example = "1")
	private Integer activityType;
	
	@ApiModelProperty(value = "団体種別", example = "1")
	private Integer groupType;
	
	@ApiModelProperty(value = "活動種別名", example = "エリマネ活動")
	private String activityTypeName;
	
	@ApiModelProperty(value = "団体種別名", example = "エキキタ")
	private String groupTypeName;
	
	@ApiModelProperty(value = "活動名", example = "XXXXXXXXXXX")
	private String activityName;
	
	@ApiModelProperty(value = "活動場所", example = "XXXXXXXXXXX")
	private String activityPlace;
	
	@ApiModelProperty(value = "活動内容", example = "XXXXXXXXXXX")
	private String activityContent;
	
	@ApiModelProperty(value = "参加人数", example = "10")
	private Integer participantCount;
	
	@ApiModelProperty(value = "備考", example = "清掃活動における備考事項")
	private String remarks;
	
	@ApiModelProperty(value = "親の活動ID", example = "1")
	private Integer parentActivityId;
	
	@ApiModelProperty(value = "経度", example = "132.48389712616301")
	private String longitude;
	
	@ApiModelProperty(value = "緯度", example = "34.39226250130868")
	private String latitude;
	
	@ApiModelProperty(value = "ユーザID", example = "1")
	private Integer postUserId;
	
	@ApiModelProperty(value = "公開フラグ", example = "1:公開 0:非公開")
	private String publishFlag;
	
	@ApiModelProperty(value = "添付ファイル")
	private List<AttachmentForm> attachmentFormList;
	
	@ApiModelProperty(value = "削除添付ファイル")
	private List<AttachmentForm> attachmentFormDeleteList;
}
