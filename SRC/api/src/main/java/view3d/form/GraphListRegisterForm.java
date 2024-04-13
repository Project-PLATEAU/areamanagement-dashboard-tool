package view3d.form;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public class GraphListRegisterForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "グラフID", example = "100")
	private Integer graphId;
	
	@ApiModelProperty(value = "グラフタイプID", example = "100")
	private Integer graphTypeId;
	
	@ApiModelProperty(value = "グラフ名", example = "エキキタ：年度別活動回数・参加人数")
	private String graphName;
	
	@ApiModelProperty(value = "ソースID", example = "100")
	private Integer sourceId;
	
	@ApiModelProperty(value = "グラフ Y軸のカラム名", example = "y_column_name")
	private String graphYColumn;
	
	@ApiModelProperty(value = "グラフ X軸のカラム名", example = "x_column_name")
	private String graphXColumn;
	
	@ApiModelProperty(value = "リストのカラム一覧", example = "{column_name:1}")
	private Map<String,Integer> columnMap;
	
	@ApiModelProperty(value = "リストのソートリスト", example = "{column_name:DESC}")
	private Map<String,String> sortModeMap;
	
	@ApiModelProperty(value = "リミット数 null(デフォルトの設定数はapplication.propertiesで指定) or リミット数", example = "12")
	private Integer limitSize;
	
	@ApiModelProperty(value = "棒グラフ 方向", example = "縦：vertical or 横：horizontal")
	private String graphDirection;
	
	@ApiModelProperty(value = "編集制限フラグ", example = "SE設定済みのグラフリストの場合：1,それ以外はnull or 0")
	private String editRestrictionFlag;
	
	@ApiModelProperty(value = "集約関数フラグ", example = "使用する場合：1,それ以外はnull or 0")
	private String groupByFlag;
	
	@ApiModelProperty(value = "集約関数タイプ", example = "1:SUM,2:AVG,3:MIN,4:MAX,5:COUNT")
	private Integer aggregationType;

}
