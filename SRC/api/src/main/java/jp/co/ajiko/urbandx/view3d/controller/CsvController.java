package jp.co.ajiko.urbandx.view3d.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jp.co.ajiko.urbandx.view3d.entity.ResponseError;
import jp.co.ajiko.urbandx.view3d.service.AttachmentsService;
import jp.co.ajiko.urbandx.view3d.service.CsvDisplayTableService;
import jp.co.ajiko.urbandx.view3d.service.CsvDownloadService;
import jp.co.ajiko.urbandx.view3d.service.CsvUpdateService;
import jp.co.ajiko.urbandx.view3d.service.SummaryUpdateService;

@RestController
@RequestMapping("/csv")
public class CsvController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CsvController.class);

	@Autowired
	CsvDisplayTableService csvDispTableService;
	@Autowired
	CsvDownloadService csvDownloadService;
	@Autowired
	CsvUpdateService csvUpdateService;
	@Autowired
	SummaryUpdateService summaryUpdateService;
	@Autowired
	AttachmentsService attachmentsService;
	@Autowired
	ResourceLoader resourceLoader;

	@RequestMapping(value = "/dispTable", method = RequestMethod.POST)
	@ApiOperation(value = "地域統計・回遊性データを取得", notes = "itemNameに対応する統計項目のデータをテーブル表示")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "itemNameが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public Map<String, Object> dispTableByItem(@ApiParam(required = true, value = "アイテム名")@RequestParam(value = "itemName", defaultValue = "") String itemName) {
		LOGGER.debug("地域統計/回遊性情報のテーブル表示処理 開始");

		Map<String, Object> res = new HashMap<>();
		try {
			if (itemName == null || itemName.isEmpty()) {
				LOGGER.warn("対象項目:未選択");
				res.put("error", "対象項目を選択してください");
			} else {
				LOGGER.debug("対象項目:{}", itemName);
				switch (itemName) {
				case "chika2":
					res = csvDispTableService.createChika2Table();
					break;
				case "erimane_ninchido":
					res = csvDispTableService.createErimaneNinchidoTable();
					break;
				case "gis_joint2_household":
					res = csvDispTableService.createGisJoint2Table(itemName);
					break;
				case "gis_joint2_population":
					res = csvDispTableService.createGisJoint2Table(itemName);
					break;
				case "gis_joint2_size":
					res = csvDispTableService.createGisJoint2Table(itemName);
					break;
				case "gis_joint2_office":
					res = csvDispTableService.createGisJoint2Table(itemName);
					break;
				case "gis_joint2_employee":
					res = csvDispTableService.createGisJoint2Table(itemName);
					break;
				case "station_users":
					res = csvDispTableService.createStationUsers();
					break;
				case "syogyoshisetsu":
					res = csvDispTableService.createSyogyoshisetsu();
					break;
				case "syokencyosa_shijiritsu":
					res = csvDispTableService.createSyokencyosaShijiritsu();
					break;
				case "city_summary":
					res = csvDispTableService.createCitySummary();
					break;
				case "kaiyusei_people":
					res = csvDispTableService.createKaiyuseiPeople();
					break;
				case "kaiyusei_age":
					res = csvDispTableService.createKaiyuseiAge();
					break;
				case "kaiyusei_gender":
					res = csvDispTableService.createKaiyuseiGender();
					break;
				case "kaiyusei_region":
					res = csvDispTableService.createKaiyuseiRegion();
					break;
				case "kaiyusei_steps":
					res = csvDispTableService.createKaiyuseiSteps();
					break;
				default:
					break;
				}
			}
			LOGGER.debug("地域統計/回遊性情報のテーブル表示処理 正常終了");
			return res;
		} catch (Exception e) {
			LOGGER.error("地域統計/回遊性情報のテーブル表示処理 異常終了", e);
			res.put("error", "システムエラーが発生しました。");
			return res;
		}
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	@ApiOperation(value = "地域統計・回遊性データをダウンロード", notes = "itemNameに対応する統計項目のデータをCSVダウンロード")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "itemNameが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public ResponseEntity<?> downloadByItem(@ApiParam(required = true, value = "アイテム名")@RequestParam(value = "itemName", defaultValue = "") String itemName) {
		LOGGER.debug("地域統計/回遊性情報のCSVダウンロード処理 開始");
		Map<String, Object> res = new HashMap<>();
		try {
			HttpHeaders headers = new HttpHeaders();

			final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
			if (itemName == null || itemName.isEmpty()) {

				res.put("error", "統計項目を選択してください");
				LOGGER.warn("対象項目:未選択");
				return ResponseEntity.badRequest().body(res);
			} else {
				String fileName = "";
				Map<String, List<String>> csv = null;
				LOGGER.debug("対象項目:{}", itemName);
				switch (itemName) {
				case "chika2":
					fileName = "chika.csv";
					csv = csvDownloadService.createChika2CsvData();
					break;
				case "erimane_ninchido":
					fileName = "ninchido.csv";
					csv = csvDownloadService.createNinchidoCsvData();
					break;
				case "gis_joint2_household":
					fileName = "jinko_setai.csv";
					csv = csvDownloadService.createGisJoint2CsvData(itemName);
					break;
				case "gis_joint2_population":
					fileName = "nenrei_jinko.csv";
					csv = csvDownloadService.createGisJoint2CsvData(itemName);
					break;
				case "gis_joint2_size":
					fileName = "setaijinin_setai.csv";
					csv = csvDownloadService.createGisJoint2CsvData(itemName);
					break;
				case "gis_joint2_office":
					fileName = "jimusyo.csv";
					csv = csvDownloadService.createGisJoint2CsvData(itemName);
					break;
				case "gis_joint2_employee":
					fileName = "jugyosya.csv";
					csv = csvDownloadService.createGisJoint2CsvData(itemName);
					break;
				case "station_users":
					fileName = "jokokyaku.csv";
					csv = csvDownloadService.createStationUsersCsvData();
					break;
				case "syogyoshisetsu":
					fileName = "syogyoshisetsu.csv";
					csv = csvDownloadService.createSyogyoshisetsu();
					break;
				case "syokencyosa_shijiritsu":
					fileName = "shijiritsu.csv";
					csv = csvDownloadService.createSyokencyosaShijiritsuCsvData();
					break;
				case "city_summary":
					fileName = "region_summary.csv";
					csv = csvDownloadService.createCitySummaryCsvData();
					break;
				case "kaiyusei_people":
					fileName = "kaiyusei_people.csv";
					csv = csvDownloadService.createKaiyuPeopleCsvData();
					break;
				case "kaiyusei_age":
					fileName = "kaiyusei_age.csv";
					csv = csvDownloadService.createKaiyuAgeCsvData();
					break;
				case "kaiyusei_gender":
					fileName = "kaiyusei_gender.csv";
					csv = csvDownloadService.createKaiyuGenderCsvData();
					break;
				case "kaiyusei_region":
					fileName = "kaiyusei_region.csv";
					csv = csvDownloadService.createKaiyuRegionCsvData();
					break;
				case "kaiyusei_steps":
					fileName = "kaiyusei_steps.csv";
					csv = csvDownloadService.createKaiyuStepsCsvData();
					break;
				default:
					break;
				}
				String headerValue = String.format(CONTENT_DISPOSITION_FORMAT,
						fileName, UriUtils.encode(fileName, StandardCharsets.UTF_8.name()));
				headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue);

				List<String> csvHeader = csv.get("header");
				List<String> csvData = csv.get("data");
				int dataColumn = csvHeader.size();
				String csvText = "";
				//ヘッダ書き込み
				for (int i = 0; i < dataColumn; i++) {
					csvText += csvHeader.get(i) + ",";
				}
				csvText += "\n";
				//データ部書き込み
				int rowNum = 1;
				for (int i = 0; i < csvData.size(); i++) {
					csvText += csvData.get(i) + ",";
					if (i == (dataColumn * rowNum - 1)) {
						csvText += "\n";
						rowNum++;
					}
				}
				LOGGER.debug("地域統計/回遊性情報のCSVダウンロード処理 正常終了");
				return new ResponseEntity<byte[]>(csvText.getBytes("MS932"), headers,
						HttpStatus.OK);
			}
		} catch (Exception e) {
			LOGGER.error("地域統計/回遊性情報のCSVダウンロード処理 異常終了", e);
			res.put("error", "システムエラーが発生しました。");
			return ResponseEntity.badRequest().body(res);
		}

	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ApiOperation(value = "地域統計・回遊性データを更新", notes = "itemNameに対応する統計項目のデータを更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "itemNameが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public Map<String, Object> updateByItem(
			@ApiParam(required = true, value = "csvファイル")@RequestParam(value = "updateFile") MultipartFile uploadFile,
			@ApiParam(required = true, value = "アイテム名")@RequestParam(value = "itemName", defaultValue = "") String itemName) {
		LOGGER.debug("地域統計/回遊性情報のCSV取込処理 開始");

		Map<String, Object> res = new HashMap<>();
		boolean updateSougouFlag = false;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadFile.getInputStream(), "UTF-8"))) {
			LOGGER.debug("対象項目:{}", itemName);

			switch (itemName) {
			case "chika2":
				csvUpdateService.updateChika2(br);
				LOGGER.debug("総合評価の更新処理 開始");
				updateSougouFlag = true;
				summaryUpdateService.updateSHR(itemName);
				break;
			case "erimane_ninchido":
				csvUpdateService.updateNinchido(br);
				break;
			case "gis_joint2_household":
				csvUpdateService.updateGisJoint2(itemName, br);
				LOGGER.debug("総合評価の更新処理 開始");
				updateSougouFlag = true;
				summaryUpdateService.updateSHR(itemName);
				break;
			case "gis_joint2_population":
				csvUpdateService.updateGisJoint2(itemName, br);
				break;
			case "gis_joint2_size":
				csvUpdateService.updateGisJoint2(itemName, br);
				break;
			case "gis_joint2_office":
				csvUpdateService.updateGisJoint2(itemName, br);
				LOGGER.debug("総合評価の更新処理 開始");
				updateSougouFlag = true;
				summaryUpdateService.updateSHR(itemName);
				break;
			case "gis_joint2_employee":
				csvUpdateService.updateGisJoint2(itemName, br);
				LOGGER.debug("総合評価の更新処理 開始");
				updateSougouFlag = true;
				summaryUpdateService.updateSHR(itemName);
				break;
			case "station_users":
				csvUpdateService.updateStationUsers(br);
				LOGGER.debug("総合評価の更新処理 開始");
				updateSougouFlag = true;
				summaryUpdateService.updateSHR(itemName);
				break;
			case "syogyoshisetsu":
				csvUpdateService.updateSyogyoshisetsu(br);
				break;
			case "syokencyosa_shijiritsu":
				csvUpdateService.updateSyokencyosaShijiritsu(br);
				break;
			case "city_summary":
				csvUpdateService.updateCitySummary(br);
				LOGGER.debug("総合評価の更新処理 開始");
				updateSougouFlag = true;
				summaryUpdateService.updateSHR(itemName);
				break;
			case "kaiyusei_people":
				csvUpdateService.updateKaiyuPeople(br);
				break;
			case "kaiyusei_age":
				csvUpdateService.updateKaiyuAge(br);
				break;
			case "kaiyusei_gender":
				csvUpdateService.updateKaiyuGender(br);
				break;
			case "kaiyusei_region":
				csvUpdateService.updateKaiyuRegion(br);
				break;
			case "kaiyusei_steps":
				csvUpdateService.updateKaiyuSteps(br);
				break;
			default:
				break;
			}
			res.put("result", "success");
			if(updateSougouFlag) {
				LOGGER.debug("総合評価の更新処理 正常終了");
			}
			LOGGER.debug("地域統計/回遊性情報のCSV取込処理 正常終了");
			return res;
		} catch (Exception e) {
			res.put("result", "failed");
			if(updateSougouFlag) {
				LOGGER.warn("総合評価の更新処理 異常終了");
			}
			LOGGER.error("地域統計/回遊性情報のCSV取込処理 異常終了", e);
			return res;
		}

	}

}
