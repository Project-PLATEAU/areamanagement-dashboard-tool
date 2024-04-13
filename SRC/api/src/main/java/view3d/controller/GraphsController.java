package view3d.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import view3d.form.ResponseEntityForm;
import view3d.form.ThemeGraphListForm;
import view3d.service.ThemeGraphListService;
import view3d.service.ThemeService;
import view3d.util.AuthUtil;

@RestController
@RequestMapping("/graphs")
public class GraphsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphsController.class);
	
	@Autowired
	ThemeGraphListService themeGraphListService;
	
	@Autowired
	ThemeService themeService;
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)グラフ・リスト一覧取得", notes = "全てのグラフ・リスト一覧情報を取得")
	@ResponseBody
	@ApiResponses(value = {@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeGraphListForm> getAllGraphs() {
		List<ThemeGraphListForm> themeGraphListFormList =  null;
		try {
			themeGraphListFormList =  themeGraphListService.findAllNoDataList();
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト一覧取得に失敗 ");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeGraphListFormList == null || themeGraphListFormList.size()<1) {
			LOGGER.error("グラフ・リスト一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeGraphListFormList;
	}

	
	@RequestMapping(value = "/{themeId}", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)グラフ・リスト一覧取得", notes = "themeIdに紐づくグラフ・リスト一覧情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeGraphListForm> getGraphsByThemeId(@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,@CookieValue(value = "token", required = false) String token) {
		List<ThemeGraphListForm> themeGraphListFormList =  null;
		Theme theme = themeService.findByThemeId(themeId);
		String role = AuthUtil.getRole(token);
		if(!"1".equals(theme.getPublishFlag()) && !"admin".equals(role)) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			themeGraphListFormList =  themeGraphListService.findByThemeId(themeId);
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト一覧取得に失敗 themeId： " + themeId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeGraphListFormList == null || themeGraphListFormList.size()<1) {
			LOGGER.error("グラフ・リスト一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeGraphListFormList;
	}
	
	@RequestMapping(value = "/{themeId}", method = RequestMethod.POST)
	@ApiOperation(value = "(権限制御あり)グラフ・リスト一覧取得", notes = "themeIdに紐づくグラフ・リスト一覧情報をgraphIdに紐づくSQLを切替項目で置き換え後、取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ThemeGraphListForm> getGraphsByThemeId(@ApiParam(required = true, value = "テーマID")@PathVariable("themeId") Integer themeId,
			@ApiParam(required = true, value = "切替項目 {切替項目名：切替項目値} ")@RequestBody Map<String, String> switchItemMap,@CookieValue(value = "token", required = false) String token) {
		List<ThemeGraphListForm> themeGraphListFormList =  null;
		Theme theme = themeService.findByThemeId(themeId);
		String role = AuthUtil.getRole(token);
		if(!"1".equals(theme.getPublishFlag()) && !"admin".equals(role)) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			themeGraphListFormList =  themeGraphListService.findByThemeId(themeId,switchItemMap);
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト一覧取得に失敗 themeId： " + themeId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(themeGraphListFormList == null || themeGraphListFormList.size()<1) {
			LOGGER.error("グラフ・リスト一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return themeGraphListFormList;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)グラフ情報を更新", notes = "グラフ・リストの設定情報(テーマグラフリスト)を更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "更新成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm updateGraphs(@ApiParam(required = true, value = "テーマグラフリストフォーム")@RequestBody List<ThemeGraphListForm> themeGraphListFormList) {
		if(themeGraphListFormList == null || themeGraphListFormList.size() < 1) {
			LOGGER.error("テーマグラフリスト一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		try {
			boolean result = themeGraphListService.updateThemeGraphListFormList(themeGraphListFormList);
			if(result) {
				return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
			}else {
				throw new Exception("processing error");
			}
		} catch (Exception e) {
			LOGGER.error("グラフ・リストの設定情報更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)グラフ情報を作成", notes = "グラフ・リストの設定情報(テーマグラフリスト)を作成")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "作成成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm registerGraphs(@ApiParam(required = true, value = "テーマグラフリストフォーム")@RequestBody List<ThemeGraphListForm> themeGraphListFormList) {
		if(themeGraphListFormList == null || themeGraphListFormList.size() < 1) {
			LOGGER.error("テーマグラフリスト一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		try {
			boolean result = themeGraphListService.registerThemeGraphListFormList(themeGraphListFormList);
			if(result) {
				return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
			}else {
				throw new Exception("processing error");
			}
		} catch (Exception e) {
			LOGGER.error("グラフ・リストの設定情報作成に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)グラフ情報を削除", notes = "グラフ・リストの設定情報(テーマグラフリスト)を削除")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "削除成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm deleteGraphs(@ApiParam(required = true, value = "テーマグラフリストフォーム")@RequestBody List<ThemeGraphListForm> themeGraphListFormList) {
		if(themeGraphListFormList == null || themeGraphListFormList.size() < 1) {
			LOGGER.error("テーマグラフリスト一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		try {
			boolean result = themeGraphListService.deleteThemeGraphListFormList(themeGraphListFormList);
			if(result) {
				return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
			}else {
				throw new Exception("processing error");
			}
		} catch (Exception e) {
			LOGGER.error("グラフ・リストの設定情報削除に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
