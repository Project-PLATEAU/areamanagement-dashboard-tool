package view3d.form;

import java.io.Serializable;
import java.util.List;

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
public class LayerSourceForm implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "ソースID", example = "100")
	private Integer sourceId;
	
	@ApiModelProperty(value = "レイヤID", example = "100")
	private Integer layerId;
	
	@ApiModelProperty(value = "テーブル名", example = "table_name")
	private String tableName;
	
	@ApiModelProperty(value = "レイヤソース定義値一覧")
	private List<LayerSourceFieldForm> layerSourceFieldFormList;
	
	@ApiModelProperty(value = "レイヤ")
	private LayerForm layerForm;
}
