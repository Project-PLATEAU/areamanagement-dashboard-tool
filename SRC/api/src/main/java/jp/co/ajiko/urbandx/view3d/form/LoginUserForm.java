package jp.co.ajiko.urbandx.view3d.form;

import java.io.Serializable;

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
public class LoginUserForm implements Serializable {

	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;

	/** ユーザID */
	@ApiModelProperty(value = "ユーザID", example = "0001")
	private String userId;
	
	/** ログインID */
	@ApiModelProperty(value = "ログインID", example = "test1234")
	private String loginId;

	/** ロールコード */
	@ApiModelProperty(value = "ロール", example = "admin")
	private String role;
	
	/** パスワード */
	@ApiModelProperty(value = "パスワード", example = "password")
	private String password;

}