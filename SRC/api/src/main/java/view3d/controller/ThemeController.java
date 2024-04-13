package view3d.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
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
import view3d.entity.ResponseError;
import view3d.entity.Theme;
import view3d.form.ThemeForm;
import view3d.service.ThemeService;
import view3d.util.AuthUtil;

@RestController
@RequestMapping("/theme")
public class ThemeController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThemeController.class);
	
	@Autowired
	ThemeService themeService;
	
	//TODO:フロントでの使用箇所は初期表示の一か所のみでgetAllThemesで代替えしているため削除予定のAPI (デザインチームに影響無いよう一旦残しておく)
	@RequestMapping(value = "/{themeId}", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)デフォルトテーマ情報取得", notes = "管理者：指定のthemeIdのデフォルトテーマ情報を取得　その他:対象のテーマが非公開の場合自動でデフォルトテーマを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ThemeForm getThemeByThemeId(@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,@CookieValue(value = "token", required = false) String token) {
		ThemeForm themeForm = null;
		try {
			String role = AuthUtil.getRole(token);
			if("admin".equals(role)) {
				themeForm = themeService.findByThemeIdForForm(themeId);
			}else {
				themeForm = themeService.findDefaultThemeByThemeId(themeId);
			}
		} catch (Exception e) {
			LOGGER.error("テーマ情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeForm == null) {
			LOGGER.error("テーマ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeForm;
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ApiOperation(value = "全てのテーマ情報を取得", notes = "公開済みの全てのテーマ情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeForm> getAllThemes() {
		List<ThemeForm> themeFormList = null;
		try {
			themeFormList = themeService.findAllLimited();
		} catch (Exception e) {
			LOGGER.error("テーマ情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeFormList == null || themeFormList.size() < 1) {
			LOGGER.error("テーマ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeFormList;
	}
	
	@RequestMapping(value = "/admin/all", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)全てのテーマ情報を取得", notes = "全てのテーマ情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeForm> getAllThemesByAdmin() {
		List<ThemeForm> themeFormList = null;
		try {
			themeFormList = themeService.findAll();
		} catch (Exception e) {
			LOGGER.error("テーマ情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeFormList == null || themeFormList.size() < 1) {
			LOGGER.error("テーマ情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeFormList;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)テーマ情報更新", notes = "themeIdに紐づくテーマ情報を更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ThemeForm updateTheme(@ApiParam(required = true, value = "テーマフォーム")@RequestBody @Validated ThemeForm themeForm,
			@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("テーマ情報の更新開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			Theme theme = themeService.findByThemeId(themeForm.getThemeId());
			themeService.update(theme, themeForm);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		}  catch (Exception e) {
			LOGGER.error("テーマ情報の更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return themeForm;
	}

}
