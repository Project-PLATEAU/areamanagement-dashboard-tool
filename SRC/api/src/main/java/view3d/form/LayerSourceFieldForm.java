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
public class LayerSourceFieldForm implements Serializable {

	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "フィールドID", example = "100")
	private Integer fieldId;
	
	@ApiModelProperty(value = "ソースID", example = "100")
	private Integer sourceId;
	
	@ApiModelProperty(value = "フィールド名", example = "spodid")
	private String fieldName;
	
	@ApiModelProperty(value = "エイリアス", example = "スポットID")
	private String alias;
}
