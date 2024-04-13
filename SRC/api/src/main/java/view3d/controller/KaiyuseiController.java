package view3d.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import view3d.entity.ResponseError;
import view3d.service.AttachmentsService;
import view3d.service.KaiyuseiService;

@RestController
@RequestMapping("/metabase/kaiyusei")
public class KaiyuseiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(KaiyuseiController.class);

	@Autowired
	KaiyuseiService kaiyuseiService;

	@Autowired
	AttachmentsService attachmentsService;
	@Autowired
	ResourceLoader resourceLoader;

	@RequestMapping(value = "/getLatestNumber", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)回遊分析最新回数を取得", notes = "回遊性の分析回数の最新回数を取得")
	@ApiResponses(value = { @ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public int getLatestNumber() {
		LOGGER.debug("回遊性ダッシュボード 最新回数取得処理 開始");

		try {
			int res = kaiyuseiService.getLatestNumber();
			LOGGER.debug("回遊性ダッシュボード 最新回数取得処理 正常終了");
			return res;
		} catch (Exception e) {
			LOGGER.error("回遊性ダッシュボード 最新回数取得処理 異常終了", e);
			return -1;
		}
	}

	@RequestMapping(value = "/getMoveNumSum", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)回遊性移動数合計データを取得", notes = "number回目の移動数合計データを取得")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "numberが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public List<Map<String, Object>> getMoveNumSum(@ApiParam(required = true, value = "回数")@RequestParam(value = "number", defaultValue = "") int number) {
		LOGGER.debug("回遊性ダッシュボード 移動数合計データ取得処理 開始");
		List<Map<String, Object>> res = new ArrayList<>();
		try {
			LOGGER.debug("回遊性ダッシュボード 移動数合計データ取得処理 正常終了");
			res=kaiyuseiService.getMoveNumSum(number);
			return res;
		} catch (Exception e) {
			LOGGER.error("回遊性ダッシュボード 移動数合計データ取得処理 異常終了", e);
			return res;
		}
	}

	@RequestMapping(value = "/getFavSpot", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)回遊性人気スポットデータを取得", notes = "number回目の人気スポットデータを取得")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "numberが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public List<Map<String, Object>> getFavSpot(@ApiParam(required = true, value = "回数")@RequestParam(value = "number", defaultValue = "") int number) {
		LOGGER.debug("回遊性ダッシュボード 人気スポットデータ取得処理 開始");
		List<Map<String, Object>> res = new ArrayList<>();
		try {
			LOGGER.debug("回遊性ダッシュボード 人気スポットデータ取得処理 正常終了");
			res=kaiyuseiService.getFavSpot(number);
			return res;
		} catch (Exception e) {
			LOGGER.error("回遊性ダッシュボード 人気スポットデータ取得処理 異常終了", e);
			return res;
		}
	}
}
