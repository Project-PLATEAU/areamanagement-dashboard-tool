package jp.co.ajiko.urbandx.view3d.form;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class DeleteActivityForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "削除対象の活動ID", example = "100")
	@NotNull
	private Integer activityId;
	
	@ApiModelProperty(value = "親の活動ID", example = "1")
	private Integer parentActivityId;
	
}
