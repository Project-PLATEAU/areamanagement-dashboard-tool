package view3d.form;

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
public class TypeListForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "活動タイプリスト")
	private List<TypeForm> ActivityTypeList;
	
	@ApiModelProperty(value = "グループタイプリスト")
	private List<TypeForm> GroupTypeList;

}