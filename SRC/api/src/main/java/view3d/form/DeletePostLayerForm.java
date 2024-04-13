package view3d.form;

import javax.validation.constraints.NotNull;

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
public class DeletePostLayerForm {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "削除対象の投稿レイヤID", example = "100")
	@NotNull
	private Integer featureId;
	
	@ApiModelProperty(value = "親の投稿レイヤID", example = "1")
	private Integer parentFeatureId;
}
