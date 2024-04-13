package view3d.form;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public class GraphListForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "グラフID", example = "100")
	private Integer graphId;
	
	@ApiModelProperty(value = "グラフタイプID", example = "100")
	private Integer graphTypeId;

	@ApiModelProperty(value = "グラフ名", example = "エキキタ：年度別活動回数・参加人数")
	private String graphName;

	@ApiModelProperty(value = "query文字列", example = "SELECT xxxx,.. FROM .. ※管理者以外はnull")
	private String queryText;
	
	@ApiModelProperty(value = "編集フラグ", example = "1:編集可能 0:編集不可")
	private String editFlag;
	
	@ApiModelProperty(value = "削除フラグ", example = "1:削除可能 0:削除不可")
	private String deleteFlag;
	
	@ApiModelProperty(value = "ソースID", example = "100")
	private Integer sourceId;
	
	@ApiModelProperty(value = "プレースホルダフラグ", example = "1:有効 0:無効")
	private String placeholderFlag;
	
	@ApiModelProperty(value = "プレビューフラグ", example = "1:有効 0:無効")
	private String previewFlag;
	
	@ApiModelProperty(value = "グラフリストデータ", example = "")
	private List<Map<String, Object>> dataList;
	
	@ApiModelProperty(value = "グラフ・テーブル設定項目値一覧")
	private List<GraphListTemplateValForm> graphListTemplateValFormList;
	
	@ApiModelProperty(value = "レイヤ_グラフ_連携一覧")
	private List<LayerGraphCooporationForm> layerGraphCooporationFormList;
	
	@ApiModelProperty(value = "レイヤソースフォーム")
	private LayerSourceForm layerSourceForm;
	
}
