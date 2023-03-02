package view3d.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

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
public class AttachmentForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "ID", example = "1")
	private Integer id;
	
	@ApiModelProperty(value = "活動ID", example = "100")
	@NotNull
	private Integer activityId;
	
	@ApiModelProperty(value = "ファイル名", example = "xxx.pdf")
	@NotNull
	private String attachmentFileName;
	
	/** アップロードファイル */
	@ApiModelProperty(value = "アップロードファイル")
	private MultipartFile uploadFile;
}
