package view3d.form;

import java.io.Serializable;

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
public class ChochomokukaiErimaneForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id", example = "100")
	private Integer id;

	@ApiModelProperty(value = "キーコード", example = "341020080")
	private String keyCode;

	@ApiModelProperty(value = "都道府県コード", example = "34")
	private String pref;

	@ApiModelProperty(value = "自治体コード", example = "102")
	private String city;

	@ApiModelProperty(value = "町丁目コード", example = "008000")
	private String sArea;

	@ApiModelProperty(value = "都道府県名", example = "広島県")
	private String prefName;

	@ApiModelProperty(value = "自治体名", example = "広島市東区")
	private String cityName;

	@ApiModelProperty(value = "町丁目名", example = "東蟹屋町")
	private String sName;

	@ApiModelProperty(value = "エリアマネジメント団体種別名", example = "エキキタ")
	private String areaManagementType;	

	@ApiModelProperty(value = "座標（経度）", example = "135.5546624")
	private Double lon;
	
	@ApiModelProperty(value = "座標（緯度）", example = "34.771873")
	private Double lat;
	
	@ApiModelProperty(value = "最小座標（経度）", example = "135.5546624")
	private Double minLon;
	
	@ApiModelProperty(value = "最小座標（緯度）", example = "34.771873")
	private Double minLat;
	
	@ApiModelProperty(value = "最大座標（経度）", example = "135.5546624")
	private Double maxLon;
	
	@ApiModelProperty(value = "最大座標（経度）", example = "34.771873")
	private Double maxLat;

}
