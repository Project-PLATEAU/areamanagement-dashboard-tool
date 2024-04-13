package view3d.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import view3d.entity.ActivityType;
import view3d.entity.GroupType;
import view3d.entity.ResponseError;
import view3d.form.ActivityForm;
import view3d.form.LayerForm;
import view3d.form.PostLayerAttributeForm;
import view3d.form.PostLayerFeatureForm;
import view3d.form.PostSearchForm;
import view3d.form.UserForm;
import view3d.service.ActivityService;
import view3d.service.ActivityTypeService;
import view3d.service.AttachmentsService;
import view3d.service.CsvDisplayTableService;
import view3d.service.CsvDownloadService;
import view3d.service.CsvUpdateService;
import view3d.service.GroupTypeService;
import view3d.service.LayerService;
import view3d.service.PostLayerService;
import view3d.service.SummaryUpdateService;
import view3d.service.UserService;

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
	@Autowired
	PostLayerService postLayerService;
	@Autowired
	UserService userService;
	@Autowired
	LayerService layerService;
	@Autowired
	ActivityService activityService;
	@Autowired
	ActivityTypeService activityTypeService;
	@Autowired
	GroupTypeService groupTypeService;

	@RequestMapping(value = "/dispTable", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)地域統計・回遊性データを取得", notes = "itemNameに対応する統計項目のデータをテーブル表示")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
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
	@ApiOperation(value = "(管理者のみ)地域統計・回遊性データをダウンロード", notes = "itemNameに対応する統計項目のデータをCSVダウンロード")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
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
	@ApiOperation(value = "(管理者のみ)地域統計・回遊性データを更新", notes = "itemNameに対応する統計項目のデータを更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
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
	
	@RequestMapping(value = "/download/postLayer/{layerId}", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)投稿レイヤのCSVデータを取得", notes = "検索に該当するCSVデータを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntity<?> getPostLayerByLayerIdAndSearchData(@ApiParam(required = true, value = "レイヤID")@PathVariable("layerId") Integer layerId,@ApiParam(required = true, value = "投稿検索フォーム")@RequestBody @Validated PostSearchForm postSearchForm) {
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		List<Integer> itemIdList = new ArrayList<>();
		List<PostLayerFeatureForm> postLayerFeatureFormList = null;
		List<PostLayerAttributeForm> postLayerAttributeFormList = null;
		LayerForm layerForm = layerService.findByLayerId(layerId);
		String csvText = "";
		try {
			//ファイル名を作成 [レイヤ名-日時].csv
			final String dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
					.format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
			final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
			String headerValue = String.format(CONTENT_DISPOSITION_FORMAT,
					layerForm.getLayerName()+"-"+dateTime+".csv", UriUtils.encode(layerForm.getLayerName()+"-"+dateTime+".csv", StandardCharsets.UTF_8.name()));
			headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue);
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
			
			//検索データ一覧を取得
			postLayerAttributeFormList = postLayerService.getPostLayerAttributeByLayerIdOrderByDispOrder(layerId);
			postLayerFeatureFormList = postLayerService.getPostLayerListByLayerIdAndSearchDataForCSV(layerId,postSearchForm);
			if(postLayerFeatureFormList == null || postLayerFeatureFormList.size() < 1 || postLayerAttributeFormList == null || postLayerAttributeFormList.size() < 1) {
				LOGGER.error("投稿レイヤのCSVダウンロード処理 該当データなし");
				res.put("error", "検索に該当するデータは存在しません");
				return ResponseEntity.badRequest().body(res);
			}
			
			//ヘッダ書き込み
			csvText +=  "公開フラグ,投稿日時,投稿者,";
			for (PostLayerAttributeForm postLayerAttributeForm : postLayerAttributeFormList) {
				csvText += postLayerAttributeForm.getItemName() + ",";
				itemIdList.add(postLayerAttributeForm.getItemId());
			}
			
			csvText += "\n";
			
			//データ部書き込み
			for (PostLayerFeatureForm postLayerFeatureForm : postLayerFeatureFormList) {
				UserForm userForm = userService.getUserFormByUserId(postLayerFeatureForm.getPostUserId());
				csvText += postLayerFeatureForm.getPublishFlag() + ",";
				csvText += csvFormatForLocalDateTime(postLayerFeatureForm.getPostDatetime()) + ",";
				if(userForm != null) {
					csvText += csvFormat(userForm.getUserName()) + ",";
				}else {
					csvText += ",";
				}
				for(Integer itemId : itemIdList){
					switch(itemId.intValue()) {
						case 1:
							csvText += csvFormat(postLayerFeatureForm.getItem1()) + ",";
							break;
						case 2:
							csvText += csvFormat(postLayerFeatureForm.getItem2()) + ",";
							break;
						case 3:
							csvText += csvFormat(postLayerFeatureForm.getItem3()) + ",";
							break;
						case 4:
							csvText += csvFormat(postLayerFeatureForm.getItem4()) + ",";
							break;
						case 5:
							csvText += csvFormat(postLayerFeatureForm.getItem5()) + ",";
							break;
						case 6:
							csvText += csvFormat(postLayerFeatureForm.getItem6()) + ",";
							break;
						case 7:
							csvText += csvFormat(postLayerFeatureForm.getItem7()) + ",";
							break;
						case 8:
							csvText += csvFormat(postLayerFeatureForm.getItem8()) + ",";
							break;
						case 9:
							csvText += csvFormat(postLayerFeatureForm.getItem9()) + ",";
							break;
						case 10:
							csvText += csvFormat(postLayerFeatureForm.getItem10()) + ",";
							break;
						default:
							break;
					}
				}
				csvText += "\n";
			}
			return new ResponseEntity<byte[]>(csvText.getBytes("MS932"), headers,
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("投稿レイヤのCSVダウンロード処理 異常終了", e);
			res.put("error", "システムエラーが発生しました。");
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	@RequestMapping(value = "/download/activity/{activityType}", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)エリマネ・イベント活動のCSVデータを取得", notes = "検索に該当するCSVデータを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntity<?> getActivityByActivityTypeAndSearchData(@ApiParam(required = true, value = "活動タイプ")@PathVariable("activityType") Integer activityType,@ApiParam(required = true, value = "投稿検索フォーム")@RequestBody @Validated PostSearchForm postSearchForm) {
		Map<String, Object> res = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		List<ActivityForm> activityFormList = null;
		String csvText = "";
		try {
			//マスタデータを取得
			List<ActivityType> activityTypeList = activityTypeService.findByAll();
			List<GroupType> groupTypeList = groupTypeService.findByAll();
			
			//ファイル名を作成 [活動タイプ名-日時].csv
			final String dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
					.format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
			final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=UTF-8''%s";
			String activityTypeName = activityTypeList.stream().filter(v -> v.getId().intValue() == activityType.intValue() )
																									.findFirst().get().getTypeName();
			String headerValue = String.format(CONTENT_DISPOSITION_FORMAT,
					activityTypeName+"-"+dateTime+".csv", UriUtils.encode(activityTypeName+"-"+dateTime+".csv", StandardCharsets.UTF_8.name()));
			headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue);
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
			
			//検索データ一覧を取得
			activityFormList = activityService.findByActivityTypeAndSearchData(activityType,postSearchForm);
			if(activityFormList == null || activityFormList.size() < 1) {
				LOGGER.error("エリマネ・イベント活動のCSVダウンロード処理 該当データなし");
				res.put("error", "検索に該当するデータは存在しません");
				return ResponseEntity.badRequest().body(res);
			}
			
			//ヘッダ書き込み
			csvText +=  "公開フラグ,投稿日時,投稿者,地域活動種別,エリアマネジメント団体,活動名,活動場所,活動内容,参加者数,開始日時,終了日時,備考,";
			csvText += "\n";
			
			//データ部書き込み
			for (ActivityForm activityForm : activityFormList) {
				UserForm userForm = userService.getUserFormByUserId(activityForm.getPostUserId());
				String groupName = groupTypeList.stream().filter(v -> v.getId().intValue() == activityForm.getActivityType().intValue() )
						.findFirst().get().getTypeName();
				csvText += activityForm.getPublishFlag() + ",";
				csvText += csvFormatForLocalDateTime(activityForm.getInsertTime()) + ",";
				if(userForm != null) {
					csvText += csvFormat(userForm.getUserName()) + ",";
				}else {
					csvText += ",";
				}
				csvText += activityTypeName + ",";
				csvText += groupName + ",";
				csvText += csvFormat(activityForm.getActivityName()) + ",";
				csvText += csvFormat(activityForm.getActivityPlace()) + ",";
				csvText += csvFormat(activityForm.getActivityContent()) + ",";
				csvText += csvFormat(activityForm.getParticipantCount()) + ",";
				csvText += csvFormatForLocalDateTime(activityForm.getStartDateAndTime()) + ",";
				csvText += csvFormatForLocalDateTime(activityForm.getEndDateAndTime()) + ",";
				csvText += csvFormat(activityForm.getRemarks()) + ",";
				csvText += "\n";
			}
			return new ResponseEntity<byte[]>(csvText.getBytes("MS932"), headers,
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("エリマネ・イベント活動のCSVダウンロード処理 異常終了", e);
			res.put("error", "システムエラーが発生しました。");
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	/**
	 * csv文字列整形用
	 * @param 対象オブジェクト
	 * @return 整形後文字列
	 */
	public String csvFormat(Object obj) {
		if(obj == null) {
			return "";
		}
		return obj.toString().replaceAll("[\r\n]", " ");
	}
	
	/**
	 * csv文字列整形用(日時)
	 * @param 日時
	 * @return 整形後文字列
	 */
	public String csvFormatForLocalDateTime(LocalDateTime localDateTime) {
		if(localDateTime == null) {
			return "";
		}
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

}
