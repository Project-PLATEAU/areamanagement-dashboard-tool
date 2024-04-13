package view3d.form;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

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
public class LayerForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "レイヤID", example = "100")
	private Integer layerId;

	@ApiModelProperty(value = "アイコンpath", example = "..xxx/xxx.png")
	private String iconPath;

	@ApiModelProperty(value = "レイヤ名", example = "エリマネ活動情報")
	private String layerName;

	@ApiModelProperty(value = "レイヤ名", example = "一般レイヤのみ.レイヤ定義をJSON形式で保持.JSONのフォーマットはterriaJSの仕様に従う.")
	private String layerSettings;

	@ApiModelProperty(value = "レイヤタイプ", example = "0:一般レイヤ 1: 投稿レイヤ")
	private Integer layerType;
	
	@ApiModelProperty(value = "プレースホルダフラグ", example = "0:無効 1: 有効")
	private String placeHolderFlag;
	
	@ApiModelProperty(value = "レイヤ_グラフ_連携一覧")
	private List<LayerGraphCooporationForm> layerGraphCooporationFormList;
	
	//フロントへの連携は不要？
	//@ApiModelProperty(value = "レイヤソース定義")
	//private LayerSourceForm layerSourceForm;

}
