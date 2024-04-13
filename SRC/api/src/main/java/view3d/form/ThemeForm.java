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
public class ThemeForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "テーマID", example = "100")
	private Integer themeId;

	@ApiModelProperty(value = "表示順", example = "1")
	private Integer dispOrder;

	@ApiModelProperty(value = "テーマ名称", example = "清掃活動履歴")
	private String themeName;
	
	@ApiModelProperty(value = "テーマグループ名称", example = "エリマネ活動")
	private String themeGroupName;

	@ApiModelProperty(value = "投稿フラグ", example = "1:有効 0:無効")
	private String postFlag;

	@ApiModelProperty(value = "公開フラグ", example = "1:公開 0:非公開")
	private String publishFlag;
	
	@ApiModelProperty(value = "案内文言", example = "案内文言が入ります")
	private String informationText;
	
	@ApiModelProperty(value = "テーマ内切替フラグ", example = "1:有効 0:無効")
	private String switchFlag;
	
	@ApiModelProperty(value = "切替項目名 カラム名(※セレクトボックスの場合、表示名に該当)", example = "xxx")
	private String switchItemNameColumnName;
	
	@ApiModelProperty(value = "切替項目値カラム名(※セレクトボックスの場合、値に該当)", example = "xxx")
	private String switchItemValueColumnName;
	
	@ApiModelProperty(value = "切替項目プレースホルダ名 (※graph_listのquery_text、layer_sourceのtable_name、layer_source_fieldのfield_nameでの置き換え対象値)", example = "count")
	private String switchPlaceholderName;
	
	@ApiModelProperty(value = "切替項目プレースホルダデフォルト値", example = "1")
	private String switchPlaceholderDefaultValue;
	
	@ApiModelProperty(value = "テーマ内切替項目一覧", example = "[{item_name:第1回目...}]")
	private List<Map<String, Object>> switchItemList;
	
}
