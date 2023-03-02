package view3d.form;

import java.io.Serializable;
import java.util.List;

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
public class ActivityListForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "活動情報の履歴ID")
	private List<ActivityForm> activityFormList;
	
}
