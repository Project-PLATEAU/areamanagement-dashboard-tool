package view3d.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import view3d.entity.ResponseError;
import view3d.form.ResponseEntityForm;
import view3d.form.UserForm;
import view3d.service.UserService;
import view3d.util.AuthUtil;
import view3d.util.LogUtil;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	/** ユーザサービスインスタンス */
	@Autowired
	protected UserService userService;
	
	/** Cookieの有効期間(秒) */
	@Value("${app.filter.cookie.expire}")
	private int expireTime;
	
	/** ログインログ csvログファイルヘッダー */
    @Value("${app.csv.log.header.login}")
    private String[] loginLogHeader;
    /** ログインログ csvログファイルパス */
    @Value("${app.csv.log.path.login}")
    private String loginLogPath;
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)全てのユーザ情報を取得", notes = "全てのユーザ情報を取得")
	@ResponseBody
	@ApiResponses(value = {@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<UserForm> getAllUsers() {
		List<UserForm> userFormList =  new ArrayList<UserForm>();
		try {
			userFormList = userService.getAllUserFormList();
		} catch (Exception e) {
			LOGGER.error("全てのユーザ情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(userFormList.size() > 0) {
			return userFormList;
		}else {
			LOGGER.error("ユーザ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/getUser/{userId}", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)ユーザ情報を取得", notes = "userIdに紐づくユーザ情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public UserForm getUser(@ApiParam(required = true, value = "ユーザID")@RequestParam("userId") Integer userId) {
		UserForm userForm = null;
		try {
			userForm = userService.getUserFormByUserId(userId);
		} catch (Exception e) {
			LOGGER.error("ユーザ情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(userForm != null) {
			return userForm;
		}else {
			LOGGER.error("ユーザ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)ユーザを新規登録", notes = "ユーザー情報を新規登録")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "更新成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm registerUser(@ApiParam(required = true, value = "ユーザフォーム")@RequestBody UserForm userForm) {
		boolean result = false;
		try {
			result = userService.register(userForm);
		} catch (Exception e) {
			LOGGER.error("ユーザ情報の登録に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(result) {
			return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "register successful.");
		}else {
			LOGGER.error("ユーザIDが重複している");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)ユーザを更新", notes = "ユーザー情報を更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "更新成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm updateUser(@ApiParam(required = true, value = "ユーザフォーム")@RequestBody UserForm userForm) {
		boolean result = false;
		try {
			result = userService.update(userForm);
		} catch (Exception e) {
			LOGGER.error("ユーザ情報の更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(result) {
			return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
		}else {
			LOGGER.error("ユーザ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/delete/{userId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "(管理者のみ)ユーザを削除", notes = "ユーザー情報を削除")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "削除成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm deleteUser(@ApiParam(required = true, value = "ユーザID")@PathVariable("userId") Integer userId) {
		boolean result = false;
		try {
			result = userService.delete(userId);
		} catch (Exception e) {
			LOGGER.error("ユーザ情報の削除に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(result) {
			return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "delete successful.");
		}else {
			LOGGER.error("ユーザ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 公開サイト認証用API（ユーザ情報を返す）
	 * 
	 * @param response HttpServletResponse
	 */
	@RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
	@ApiOperation(value = "認証", notes = "認証状態の確認")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 401, message = "認証エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseEntityForm.class) })
	public UserForm checkAuthPC(HttpServletResponse response, @CookieValue(value = "token", required = false) String token) {
		LOGGER.info("認証 開始");
		UserForm userForm = null;
		try {
			if (!AuthUtil.validate(token)) {
				LOGGER.warn("認証情報が不正");
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}
			//ユーザ情報取得する
			int userId = AuthUtil.getUserId(token);
			if(userId != 0) {
				userForm = userService.getUserFormByUserId(userId);
				response.setStatus(HttpServletResponse.SC_OK);
				return userForm;
			}else {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOGGER.info("認証 終了");
		}
	}

	/**
	 * 公開サイトログイン用Api（ユーザー情報を返す）
	 * 
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation(value = "ユーザログイン", notes = "ログインする.")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 201, message = "処理成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseEntityForm.class),
			@ApiResponse(code = 401, message = "認証エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseEntityForm.class) })
	public UserForm loginPC(HttpServletResponse response, @ApiParam(required = true, value = "ユーザフォーム")@RequestBody UserForm inputForm) {
		LOGGER.info("ユーザログイン 開始");
		try {
			String id = inputForm.getLoginId();
			String password = inputForm.getPassword();
			if (id != null && !"".equals(id) //
					|| password != null && !"".equals(password)) {
				List<UserForm> userFormList = userService.getLoginUserFormList(inputForm.getLoginId(),
						inputForm.getPassword());
				if (userFormList.size() == 0) {
					LOGGER.warn("ID、パスワードからユーザ情報が取得できなかった");
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
				} else if (userFormList.size() > 1) {
					// ユーザ情報が複数取れるのは異常
					LOGGER.warn("ID、パスワードからユーザ情報が複数取得された");
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
				}
				UserForm userForm = userFormList.get(0);
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
				return userForm;
			} else {
				// パラメータ不正
				LOGGER.warn("パラメータ不正");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception ex) {
			LOGGER.error("ユーザログイン処理で例外発生", ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOGGER.info("ユーザログイン 終了");
		}
	}
	
	/**
	 * 管理者画面用ログイン用Api（ユーザー情報を返す）
	 * 
	 */
	@RequestMapping(value = "/admin/login", method = RequestMethod.POST)
	@ApiOperation(value = "（管理者用）ユーザログイン", notes = "ログインする.")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 201, message = "処理成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseEntityForm.class),
			@ApiResponse(code = 401, message = "認証エラー", response = ResponseEntityForm.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseEntityForm.class) })
	public UserForm loginAdmin(HttpServletResponse response, @ApiParam(required = true, value = "ユーザフォーム")@RequestBody UserForm inputForm) {
		LOGGER.info("管理者　ユーザログイン 開始");
		try {
			String id = inputForm.getLoginId();
			String password = inputForm.getPassword();
			if (id != null && !"".equals(id) //
					|| password != null && !"".equals(password)) {
				List<UserForm> userFormList = userService.getLoginUserFormList(inputForm.getLoginId(),
						inputForm.getPassword());
				if (userFormList.size() == 0) {
					LOGGER.warn("ID、パスワードからユーザ情報が取得できなかった");
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
				} else if (userFormList.size() > 1) {
					// ユーザ情報が複数取れるのは異常
					LOGGER.warn("ID、パスワードからユーザ情報が複数取得された");
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
				}
				UserForm userForm = userFormList.get(0);
				if(userForm.getRole() == null || "user".equals(userForm.getRole()) || "erimane".equals(userForm.getRole())) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
				}
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
				return userForm;
			} else {
				// パラメータ不正
				LOGGER.warn("パラメータ不正");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception ex) {
			LOGGER.error("管理者　ユーザログイン処理で例外発生", ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			LOGGER.info("管理者　ユーザログイン 終了");
		}
	}
	
}
