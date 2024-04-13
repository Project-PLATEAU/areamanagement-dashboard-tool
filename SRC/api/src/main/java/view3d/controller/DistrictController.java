package view3d.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import view3d.entity.ChochomokukaiErimane;
import view3d.entity.ResponseError;
import view3d.form.ChochomokukaiErimaneForm;
import view3d.form.UserForm;
import view3d.service.SearchService;

@RestController
@RequestMapping("/district")
public class DistrictController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DistrictController.class);
	
	@Autowired
	SearchService searchService;
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ApiOperation(value = "町丁目検索", notes = "町丁目を検索")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public List<ChochomokukaiErimane> searchDistrict(@ApiParam(required = true, value = "町名")@RequestParam("townName") String town, @ApiParam(required = true, value = "丁名")@RequestParam("blockName") String block){
		try {
			// 町丁目一覧一覧を取得
			List<ChochomokukaiErimane> chochomokukaiErimaneFormLList =  searchService.searchChochomoku(town, block);
			return chochomokukaiErimaneFormLList;
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("町丁目検索に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/getChochomoku", method = RequestMethod.GET)
	@ApiOperation(value = "町丁目一覧取得", notes = "町一覧及び丁一覧を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public Map<String, Object> getChochomokuList() {
		final Map<String, Object> res = new HashMap<>();
		try {
			// 町名一覧を取得
			final List<String> townList =  searchService.getTownList();
			// 町丁一覧を取得
			final List<Object> chochomoku =  searchService.getChochomoku();
			res.put("townList", townList);
			res.put("chochomoku", chochomoku);
			return res;
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("町丁目一覧を取得に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
