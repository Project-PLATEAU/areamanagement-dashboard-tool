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
public class PostSearchForm  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@ApiModelProperty(value = "開始投稿日時", example = "2022-01-01T10:00")
	private LocalDateTime startPostDateAndTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@ApiModelProperty(value = "終了投稿日時", example = "2022-01-01T15:00")
	private LocalDateTime endPostDateAndTime;
	
	@ApiModelProperty(value = "ソートフラグ", example = "1:降順 0:昇順")
	private String sortFlag;
}
