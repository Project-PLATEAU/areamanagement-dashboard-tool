package view3d.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import view3d.form.GraphListForm;
import view3d.form.GraphListRegisterForm;
import view3d.form.GraphListTypeForm;
import view3d.form.ResponseEntityForm;
import view3d.service.GraphListService;
import view3d.service.GraphListTypeService;

@RestController
@RequestMapping("/graph")
public class GraphController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphController.class);
	
	@Autowired
	GraphListService graphListService;
	
	@Autowired
	GraphListTypeService graphListTypeService;
	
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)グラフ・リスト情報全件取得", notes = "グラフ・リスト情報を全件取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<GraphListForm> getGraphListByGraphId() {
		List<GraphListForm> graphListFormList = null;
		try {
			graphListFormList = graphListService.findAllNoDataList();
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(graphListFormList == null || graphListFormList.size() < 1) {
			LOGGER.error("グラフ・リスト情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return graphListFormList;
	}
	
	@RequestMapping(value = "/admin/{graphListId}", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)グラフ・リスト情報取得", notes = "指定のgraphListIdのグラフ・リスト情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public GraphListForm getGraphListByGraphIdForAdmin(@ApiParam(required = true, value = "グラフID")@PathVariable("graphListId") Integer graphListId) {
		GraphListForm graphListForm = null;
		try {
			graphListForm = graphListService.findByGraphListIdForAdmin(graphListId);
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(graphListForm == null) {
			LOGGER.error("グラフ・リスト情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return graphListForm;
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)グラフ・リスト情報を新規作成及び更新", notes = "グラフ・リスト情報及びグラフ・リストテンプレート設定項目値を作成及び更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public GraphListForm registerGraphList(@ApiParam(required = true, value = "グラフリストフォーム")@RequestBody GraphListRegisterForm graphListRegisterForm) {
		try {
			Integer graphId = graphListService.register(graphListRegisterForm);
			GraphListForm graphListForm = graphListService.findByGraphListIdForAdmin(graphId);
			if(graphListForm != null) {
				return graphListForm;
			}else {
				LOGGER.error("グラフ・リスト情報の新規作成に失敗");
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト情報の新規作成に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/preview", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)グラフ・リスト情報のプレビューデータを取得", notes = "グラフ・リスト情報のプレビューデータを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public GraphListForm previewGraphList(@ApiParam(required = true, value = "グラフリストフォーム")@RequestBody GraphListRegisterForm graphListRegisterForm) {
		GraphListForm graphListForm = null;
		try {
			if(!"1".equals(graphListRegisterForm.getEditRestrictionFlag())){
				graphListForm = graphListService.preview(graphListRegisterForm);
			}else {
				graphListForm = graphListService.editRestrictionPreview(graphListRegisterForm);
			}
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト情報の取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(graphListForm == null) {
			LOGGER.error("グラフ・リスト情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return graphListForm;
	}
	
	@RequestMapping(value = "/delete/{graphListId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "(管理者のみ)グラフ・リスト情報を削除", notes = "指定のgraphListIdのグラフ・リスト情報及びグラフ・リストテンプレート設定項目値を削除")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "削除成功", response = ResponseEntityForm.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm deleteGraphTabel(@ApiParam(required = true, value = "グラフID")@PathVariable("graphListId") Integer graphListId) {
		try {
			boolean result = graphListService.deleteByGraphListIdForAdmin(graphListId);
			if(result) {
				return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
			}else {
				throw new Exception("processing error");
			}
		} catch (Exception e) {
			LOGGER.error("グラフ・リスト情報の削除に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/type/all", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)グラフ・リストタイプ情報取得", notes = "全てのグラフ・リストタイプ情報取得")
	@ResponseBody
	@ApiResponses(value = {@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<GraphListTypeForm> getAllGraphTableType() {
		List<GraphListTypeForm> graphListTypeFormList = null;
		try {
			graphListTypeFormList = graphListTypeService.findAll();
		} catch (Exception e) {
			LOGGER.error("グラフ・リストタイプの情報取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(graphListTypeFormList == null || graphListTypeFormList.size()<1) {
			LOGGER.error("グラフ・リストタイプ一覧が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return graphListTypeFormList;
	}
	
}
