package jp.co.ajiko.urbandx.view3d.controller;

import java.time.LocalDateTime;
import java.util.List;

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
import jp.co.ajiko.urbandx.view3d.form.LoginUserForm;
import jp.co.ajiko.urbandx.view3d.form.ResponseEntityForm;
import jp.co.ajiko.urbandx.view3d.service.AuthenticationService;
import jp.co.ajiko.urbandx.view3d.util.AuthUtil;
import jp.co.ajiko.urbandx.view3d.util.LogUtil;

@RestController
@RequestMapping("/auth")
public class AuthenticationApiController {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationApiController.class);

	/** Cookieの有効期間(秒) */
	@Value("${app.filter.cookie.expire}")
	private int expireTime;

	/** 認証系サービスインスタンス */
	@Autowired
	protected AuthenticationService authenticationService;
	
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
	@ApiResponses(value = { @ApiResponse(code = 200, message = "認証がOKの場合", response = ResponseEntityForm.class),
			@ApiResponse(code = 401, message = "認証がNGの場合", response = ResponseEntityForm.class),
			@ApiResponse(code = 503, message = "処理エラー", response = ResponseEntityForm.class) })
	public void checkAuth(HttpServletResponse response, @CookieValue(value = "token", required = false) String token) {
		LOGGER.info("認証 開始");
		try {
			if (!AuthUtil.validate(token)) {
				LOGGER.warn("認証情報が不正");
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		} finally {
			LOGGER.info("認証 終了");
		}
	}

	/**
	 * ユーザログイン
	 * 
	 * @param LoginUserForm ログインID,パスワード
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation(value = "ユーザログイン", notes = "認証画面にログインする.")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 201, message = "認証OK", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "RequestBodyが不正な場合", response = ResponseEntityForm.class),
			@ApiResponse(code = 401, message = "認証エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 503, message = "処理エラー", response = ResponseEntityForm.class) })
	public void loginGoverment(HttpServletResponse response, @ApiParam(required = true, value = "ログインユーザーフォーム")@RequestBody LoginUserForm loginUserForm) {
		LOGGER.info("ユーザログイン 開始");
		try {
			String id = loginUserForm.getLoginId();
			String password = loginUserForm.getPassword();
			if (id != null && !"".equals(id) //
					|| password != null && !"".equals(password)) {
				List<LoginUserForm> userFormList = authenticationService.getLoginUserList(loginUserForm.getLoginId(),
						loginUserForm.getPassword());
				if (userFormList.size() == 0) {
					LOGGER.warn("ID、パスワードからユーザ情報が取得できなかった");
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
				} else if (userFormList.size() > 1) {
					// ユーザ情報が複数取れるのは異常
					LOGGER.warn("ID、パスワードからユーザ情報が複数取得された");
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
				}
				LoginUserForm userForm = userFormList.get(0);
				// ユーザ情報設定
				response.addCookie(AuthUtil.createUserCookie(userForm.getUserId(), userForm.getLoginId(),
						userForm.getRole(), expireTime));
				response.setStatus(HttpServletResponse.SC_CREATED);
				// ログインログ出力
                try {
                        Object[] logData = {LogUtil.localDateTimeToString(LocalDateTime.now()),userForm.getLoginId(),userForm.getRole()};
                        LogUtil.writeLogToCsv(loginLogPath, loginLogHeader, logData);
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
			} else {
				// パラメータ不正
				LOGGER.warn("パラメータ不正");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			LOGGER.error("ユーザログイン処理で例外発生", ex);
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		} finally {
			LOGGER.info("ユーザログイン 終了");
		}
	}
}
