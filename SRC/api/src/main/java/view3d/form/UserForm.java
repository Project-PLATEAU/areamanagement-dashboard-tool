package view3d.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザフォーム
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserForm implements Serializable {

	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;

	/** ユーザID */
	@ApiModelProperty(value = "ユーザID", example = "1 (新規の場合はnull)")
	private Integer userId;
	
	/** ログインID */
	@ApiModelProperty(value = "ログインID", example = "test1234")
	private String loginId;

	/** ロールコード */
	@ApiModelProperty(value = "ロール", example = "admin")
	private String role;
	
	/** パスワード */
	@ApiModelProperty(value = "パスワード", example = "password")
	private String password;
	
	/** ユーザ名 */
	@ApiModelProperty(value = "ユーザ名", example = "user1")
	private String userName;
	
	/** メールアドレス */
	@ApiModelProperty(value = "メールアドレス", example = "sample@xxxxxx.com")
	private String mailAddress;

}