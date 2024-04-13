package view3d.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import view3d.form.ResponseEntityForm;
import view3d.form.UserForm;
import view3d.service.UserService;
import view3d.util.AuthUtil;
import view3d.util.LogUtil;

@RestController
@RequestMapping("/auth")
public class AuthenticationApiController {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationApiController.class);

	/** Cookieの有効期間(秒) */
	@Value("${app.filter.cookie.expire}")
	private int expireTime;

	/** ユーザサービスインスタンス */
	@Autowired
	protected UserService userService;
	
	/** ログインログ csvログファイルヘッダー */
    @Value("${app.csv.log.header.login}")
    private String[] loginLogHeader;
    /** ログインログ csvログファイルパス */
    @Value("${app.csv.log.path.login}")
    private String loginLogPath;

	/**
	 * 認証用API
	 * 
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
	@ApiOperation(value = "認証", notes = "認証状態の確認")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 401, message = "認証エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseEntityForm.class) })
	public void checkAuth(HttpServletResponse response, @CookieValue(value = "token", required = false) String token) {
		LOGGER.info("認証 開始");
		try {
			if (!AuthUtil.validate(token)) {
				LOGGER.warn("認証情報が不正");
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOGGER.info("認証 終了");
		}
	}
	
	/**
	 * （管理者用）認証用API
	 * 
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/checkAdminAuth", method = RequestMethod.GET)
	@ApiOperation(value = "（管理者用）認証", notes = "認証状態の確認")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 401, message = "認証エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseEntityForm.class) })
	public void checkAdminAuth(HttpServletResponse response, @CookieValue(value = "token", required = false) String token) {
		LOGGER.info("認証 開始");
		try {
			if (!AuthUtil.validate(token) || !"admin".equals(AuthUtil.getRole(token))) {
				LOGGER.warn("認証情報が不正");
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOGGER.info("認証 終了");
		}
	}
	
	/**
	* ログアウトAPI
	* 
	* @param response HttpServletResponse
	*/
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ApiOperation(value = "ログアウト", notes = "ログアウトを実施.")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 200, message = "ログアウト完了", response = ResponseEntityForm.class) })
	public void logout(HttpServletResponse response) {
	    LOGGER.info("ログアウト 開始");
	    try {
	        Cookie cookie = new Cookie(AuthUtil.TOKEN, "");
	        cookie.setMaxAge(0);
	        cookie.setPath("/");
	        response.addCookie(cookie);
	        response.setStatus(HttpServletResponse.SC_OK);
	    } finally {
	        LOGGER.info("ログアウト 終了");
	    }
	}
}
