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
public class GraphListTemplateValForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "グラフID", example = "100")
	private Integer graphId;

	@ApiModelProperty(value = "項目ID", example = "100")
	private Integer itemId;
	
	@ApiModelProperty(value = "項目値", example = "#191970")
	private String itemValue;
	
	@ApiModelProperty(value = "グラフ・テーブル設定項目情報")
	private GraphListTemplateSettingForm graphListTemplateSettingForm;

}
