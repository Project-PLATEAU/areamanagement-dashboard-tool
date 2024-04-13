package view3d.form;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class PostLayerFeatureForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "フィーチャーID", example = "100")
	private Integer featureId;
	
	@ApiModelProperty(value = "レイヤID", example = "100")
	private Integer layerId;
	
	@ApiModelProperty(value = "Geometry文字列", example = "XXXXXXXXXXX")
	private String geom;
	
	@ApiModelProperty(value = "経度", example = "132.48389712616301")
	private String longitude;
	
	@ApiModelProperty(value = "緯度", example = "34.39226250130868")
	private String latitude;
	
	@ApiModelProperty(value = "公開フラグ", example = "1:公開 0:非公開")
	private String publishFlag;
	
	@ApiModelProperty(value = "投稿ユーザID", example = "1")
	private Integer postUserId;
	
	@ApiModelProperty(value = "親フィーチャーID", example = "100")
	private Integer parentFeatureId;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@ApiModelProperty(value = "投稿日時", example = "2022-01-01T15:00")
	private LocalDateTime postDatetime;
	
	@ApiModelProperty(value = "項目1", example = "xxxxx")
	private String item1;
	
	@ApiModelProperty(value = "項目2", example = "xxxxx")
	private String item2;
	
	@ApiModelProperty(value = "項目3", example = "xxxxx")
	private String item3;
	
	@ApiModelProperty(value = "項目4", example = "xxxxx")
	private String item4;
	
	@ApiModelProperty(value = "項目5", example = "xxxxx")
	private String item5;
	
	@ApiModelProperty(value = "項目6", example = "xxxxx")
	private String item6;
	
	@ApiModelProperty(value = "項目7", example = "xxxxx")
	private String item7;
	
	@ApiModelProperty(value = "項目8", example = "xxxxx")
	private String item8;
	
	@ApiModelProperty(value = "項目9", example = "xxxxx")
	private String item9;
	
	@ApiModelProperty(value = "項目10", example = "xxxxx")
	private String item10;
	
//	@ApiModelProperty(value = "投稿レイヤ属性一覧")
//	private List<PostLayerAttributeForm> postLayerAttributeFormList;

}
