package view3d.form;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;

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
public class ThemeLayerForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "テーマID", example = "100")
	private Integer themeId;

	@ApiModelProperty(value = "レイヤID", example = "100")
	private Integer layerId;
	
	@ApiModelProperty(value = "表示順", example = "1")
	private Integer dispOrder;

	@ApiModelProperty(value = "投稿フラグ", example = "1:有効 0:無効")
	private String postFlag;
	
	@ApiModelProperty(value = "レイヤ")
	private LayerForm layerForm;
}
