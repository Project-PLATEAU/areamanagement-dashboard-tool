package view3d.form;

import java.io.Serializable;

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
public class LayerGraphCooporationForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "連携ID", example = "100")
	private Integer cooperationId;

	@ApiModelProperty(value = "連携種別", example = "0: ダッシュボード->3D都市モデルビューワ 1: 3D都市モデルビューワ -> ダッシュボード 2: 3D都市モデルビューワ <-> ダッシュボード")
	private Integer cooperationType;
	
	@ApiModelProperty(value = "連携オプション", example = "json文字列")
	private String cooperationOption;

	@ApiModelProperty(value = "グラフID", example = "100")
	private Integer graphId;

	@ApiModelProperty(value = "レイヤID", example = "100")
	private Integer layerId;
}
