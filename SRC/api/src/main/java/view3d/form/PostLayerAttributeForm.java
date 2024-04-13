package view3d.form;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class PostLayerAttributeForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "レイヤID", example = "100")
	private Integer layerId;
	
	@ApiModelProperty(value = "項目ID", example = "100")
	private Integer itemId;
	
	@ApiModelProperty(value = "項目名", example = "開催名")
	private String itemName;
	
	@ApiModelProperty(value = "項目種別", example = "1: テキスト(小) 2: テキスト(大) 3:数値 4:写真")
	private Integer itemType;
	
	@ApiModelProperty(value = "表示順", example = "1")
	private Integer dispOrder;

}
