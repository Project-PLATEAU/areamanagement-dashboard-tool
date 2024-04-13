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
public class GraphListTemplateSettingForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "項目ID", example = "100")
	private Integer itemId;

	@ApiModelProperty(value = "グラフタイプID", example = "100")
	private Integer graphTypeId;
	
	@ApiModelProperty(value = "属性名", example = "backgroundColor")
	private String attributeName;

	@ApiModelProperty(value = "属性タイプ", example = "1:string,2:number,3:array[string],4:array[number],5:object")
	private Integer attributeType;

	@ApiModelProperty(value = "表示名", example = "背景色")
	private String displayName;

	@ApiModelProperty(value = "表示タイプ", example = "1:text,2:color,3:slider,4:list")
	private Integer displayType;

	@ApiModelProperty(value = "グループタイプ", example = "1")
	private Integer groupType;
	
	@ApiModelProperty(value = "プレースホルダフラグ", example = "1:有効 0:無効")
	private String placeholderFlag;
	
}
