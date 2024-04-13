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
public class GraphListTypeForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "グラフタイプID", example = "100")
	private Integer graphTypeId;

	@ApiModelProperty(value = "グラフタイプ名", example = "混合グラフ")
	private String graphTypeName;
	
	@ApiModelProperty(value = "編集フラグ", example = "1:編集で選択可能 0:編集で選択不可")
	private String editFlag;
	
	@ApiModelProperty(value = "グラフ・テーブル設定項目一覧")
	private List<GraphListTemplateSettingForm> graphListTemplateSettingFormList;
}
