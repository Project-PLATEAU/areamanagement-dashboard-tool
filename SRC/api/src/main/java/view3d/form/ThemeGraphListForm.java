package view3d.form;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
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
public class ThemeGraphListForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "テーマID", example = "100")
	private Integer themeId;

	@ApiModelProperty(value = "グラフID", example = "100")
	private Integer graphId;
	
	@ApiModelProperty(value = "パネル高さ(グリッド単位)", example = "3")
	private Integer panelHeight;

	@ApiModelProperty(value = "パネル幅(グリッド単位)", example = "4")
	private Integer panelWidth;

	@ApiModelProperty(value = "パネルx座標(グリッド単位)", example = "0")
	private Integer topLeftX;

	@ApiModelProperty(value = "パネルｙ座標(グリッド単位)", example = "3")
	private Integer topLeftY;
	
	@ApiModelProperty(value = "グラフ・リスト")
	private GraphListForm graphListForm;
}
