package view3d.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.util.IOUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import view3d.entity.Activity;
import view3d.entity.ActivityType;
import view3d.entity.Attachment;
import view3d.entity.GroupType;
import view3d.entity.PostLayerIconPath;
import view3d.entity.ResponseError;
import view3d.form.ActivityForm;
import view3d.form.ActivityHistoryListForm;
import view3d.form.AttachmentForm;
import view3d.form.DeleteActivityForm;
import view3d.form.LayerForm;
import view3d.form.PostLayerFeatureForm;
import view3d.form.PostSearchForm;
import view3d.form.ResponseEntityForm;
import view3d.form.TypeForm;
import view3d.form.TypeListForm;
import view3d.service.ActivityService;
import view3d.service.ActivityTypeService;
import view3d.service.AttachmentsService;
import view3d.service.GroupTypeService;
import view3d.util.AuthUtil;

@RestController
@RequestMapping("/activity")
public class ActivityController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	ActivityService activityService;

	@Autowired
	AttachmentsService attachmentsService;

	@Autowired
	ActivityTypeService activityTypeService;

	@Autowired
	GroupTypeService groupTypeService;

	@Autowired
	ResourceLoader resourceLoader;

	@Value("${app.file.rootpath}")
	protected String fileRootPath;
	
	@Value("${app.billboard.activity.icons.rootpath}")
	protected String iconRootPath;
	
	@Value("${app.billboard.icons.activity.settings}")
	protected String iconSettings;

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)活動情報を取得", notes = "activityIdに紐づく活動情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ActivityForm getActivity(@ApiParam(required = true, value = "活動ID")@RequestParam("activityId") Integer activityId,@CookieValue(value = "token", required = false) String token) {
		ActivityForm activityForm = null;
		try {
			activityForm = activityService.findByActivityId(activityId);
		} catch (Exception e) {
			LOGGER.error("活動情報の取得に失敗 activityId： " + activityId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(activityForm == null || activityForm.getActivityId() == null) {
			LOGGER.error("活動情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		if(!AuthUtil.postViewAuthorityCheck(token, activityForm.getPostUserId(), activityForm.getPublishFlag())) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		return activityForm;
	}
	
	@RequestMapping(value = "/all/{activityType}", method = RequestMethod.GET)
	@ApiOperation(value = "(管理者のみ)活動情報一覧取得", notes = "活動typeIDに紐づくエリマネ・イベント活動一覧（未承認のものも含む）取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ActivityForm> getAllActivityByActivityType(@ApiParam(required = true, value = "活動タイプ")@PathVariable("activityType") Integer activityType) {
		List<ActivityForm> activityFormList = null;
		try {
			activityFormList = activityService.findByActivityType(activityType);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("活動情報一覧の取得に失敗 activityType： " + activityType);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(activityFormList == null || activityFormList.size() < 1) {
			LOGGER.error("活動情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return activityFormList;
	}
	
	@RequestMapping(value = "/search/{activityType}", method = RequestMethod.POST)
	@ApiOperation(value = "(管理者のみ)活動情報一覧検索", notes = "検索データに該当するエリマネ・イベント活動一覧（未承認のものも含む）取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public List<ActivityForm> getAllActivityByActivityTypeAndSearchData(@ApiParam(required = true, value = "活動タイプ")@PathVariable("activityType") Integer activityType,@ApiParam(required = true, value = "投稿検索フォーム")@RequestBody @Validated PostSearchForm postSearchForm) {
		List<ActivityForm> activityFormList = null;
		try {
			activityFormList = activityService.findByActivityTypeAndSearchData(activityType,postSearchForm);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("活動情報一覧の検索に失敗 activityType： " + activityType);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(activityFormList == null || activityFormList.size() < 1) {
			LOGGER.error("該当する活動情報が存在しない");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return activityFormList;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(権限制御あり)活動情報を登録・更新", notes = "activityIdに紐づく活動情報が存在しない場合は登録、activityIdに紐づく活動情報が存在する場合は更新")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 202, message = "更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ActivityForm registerActivity(@ApiParam(required = true, value = "活動フォーム")@RequestBody @Validated ActivityForm activityForm,@ApiParam(hidden = true)Errors erros,@CookieValue(value = "token", required = false) String token) {
		LOGGER.info("活動情報の登録・更新開始");
		if (erros.hasErrors()) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		Activity activity = new Activity();
		Optional<Integer> idOps = Optional.ofNullable(activityForm.getActivityId());
		if (idOps.isPresent() && idOps.get().intValue() != 0) {
			Optional<Activity> activityOpt = activityService.findByActivityIdForEntity(idOps.get());
			if (activityOpt.isPresent()) {
				activity = activityOpt.get();
				if(!AuthUtil.postUpdateAuthorityCheck(token, activity.getPostUserId(), activity.getPublishFlag())) {
					LOGGER.error("権限エラー");
					throw new ResponseStatusException(HttpStatus.FORBIDDEN);
				}
				try {
					String role = AuthUtil.getRole(token);
					if("1".equals(activity.getPublishFlag()) &&  ("admin".equals(role) || "erimane".equals(role))) {
						activityForm.setPublishFlag("1");
					}else {
						activityForm.setPublishFlag("0");
					}
					activityService.update(activity, activityForm);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					LOGGER.error("活動情報の登録・更新に失敗 activityId： " + activityForm.getActivityId());
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.ACCEPTED);
			}
		} else {
			if(!AuthUtil.postRegisterAuthorityCheck(token)) {
				LOGGER.error("権限エラー");
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			try {
				activityForm = activityService.insert(activityForm);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				LOGGER.error("活動情報の登録・更新に失敗 activityId： " + activityForm.getActivityId());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		LOGGER.info("活動情報の登録・更新終了");
		return activityForm;
	}
	
	@RequestMapping(value = "/publish", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "(管理者のみ)活動情報公開設定更新", notes = "活動情報の公開設定情報を更新")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 204, message = "処理成功", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエストが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public ResponseEntityForm updatePublish(@ApiParam(required = true, value = "活動情報リスト")@RequestBody List<ActivityForm> activityFormList) {
		boolean result = false;
		try {
			result = activityService.updatePublish(activityFormList);
			if(result) {
				return new ResponseEntityForm(HttpStatus.NO_CONTENT.value(), "update successful.");
			}else {
				LOGGER.error("活動情報公開設定更新に失敗");
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOGGER.error("活動情報公開設定更新に失敗");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ApiOperation(value = "(権限制御あり)活動情報を削除", notes = "活動情報を削除し新たなparentActivityIdを取得")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 202, message = "更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public DeleteActivityForm deleteActivity(@ApiParam(required = true, value = "活動削除フォーム")@RequestBody @Validated DeleteActivityForm activityForm,@ApiParam(hidden = true)Errors erros,@CookieValue(value = "token", required = false) String token) {
		LOGGER.info("活動情報の削除開始");
		if (erros.hasErrors()) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if (activityForm.getActivityId() > 0 && activityForm.getParentActivityId() > 0) {
			Optional<Activity> activityOpt = activityService.findByActivityIdForEntity(activityForm.getActivityId());
			if (!activityOpt.isPresent()) {
				throw new ResponseStatusException(HttpStatus.ACCEPTED);
			}
			//更新権限チェックを実施
			if(!AuthUtil.postDeleteAuthorityCheck(token, activityOpt.get().getPostUserId(), activityOpt.get().getPublishFlag())) {
				LOGGER.error("権限エラー");
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			Integer newParentActivityId = null;
			try {
				newParentActivityId = activityService.delete(activityForm);
			} catch (Exception e) {
				LOGGER.error("活動情報の削除に失敗 activityId： " + activityForm.getActivityId());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOGGER.info("活動情報の削除終了");
			return new DeleteActivityForm(activityForm.getActivityId(), newParentActivityId);
		} else {
			throw new ResponseStatusException(HttpStatus.ACCEPTED);
		}
	}

	@RequestMapping(value = "/type", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "活動タイプ・グループタイプを取得", notes = "登録済みの活動タイプ・グループタイプを取得")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public TypeListForm getActivityTypeList() {
		List<ActivityType> activityTypeList = activityTypeService.findByAll();
		List<TypeForm> activityTypeFormList = new ArrayList<>();
		for (ActivityType activityType : activityTypeList) {
			activityTypeFormList.add(new TypeForm(activityType.getId(), activityType.getTypeName()));
		}

		List<GroupType> groupTypeList = groupTypeService.findByAll();
		List<TypeForm> groupTypeFormList = new ArrayList<>();
		for (GroupType groupType : groupTypeList) {
			groupTypeFormList.add(new TypeForm(groupType.getId(), groupType.getTypeName()));
		}
		LOGGER.info("活動タイプ・グループタイプの取得");
		return new TypeListForm(activityTypeFormList, groupTypeFormList);
	}

	@RequestMapping(value = "/activity_history", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)活動情報の履歴を取得", notes = "parentActivityIdとactivityTypeに紐づくactivityIdと開始日時一覧を取得")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ActivityHistoryListForm getActivityHistory(@ApiParam(required = true, value = "親活動ID")@RequestParam("parentActivityId") Integer parentActivityId,
			@ApiParam(required = true, value = "活動タイプ")@RequestParam("activityType") Integer activityType,@CookieValue(value = "token", required = false) String token) {
		try {
			ActivityHistoryListForm activityIdListForm = new ActivityHistoryListForm();
			List<Activity> activityList = activityService.findByParentActivityIdAndActivityTypeForHistory(parentActivityId,
					activityType);
			List<ActivityForm> activityFormList = new ArrayList<>();
			for (Activity activity : activityList) {
				try {
					if(AuthUtil.postViewAuthorityCheck(token, activity.getPostUserId(), activity.getPublishFlag())) {
						ActivityForm activityForm = new ActivityForm();
						activityForm.setActivityId(activity.getActivityId());
						activityForm.setStartDateAndTime(activity.getStartDateAndTime());
						activityFormList.add(activityForm);
					}
				} catch (Exception e) {
					LOGGER.error("活動情報の履歴情報取得に失敗 activityId： " + activity.getActivityId() + " , activityType:" + activityType);
				}
			}
			activityIdListForm.setActivityFormList(activityFormList);
			return activityIdListForm;
		} catch (Exception e) {
			LOGGER.error("活動情報の履歴情報取得に失敗 parentActivityId： " + parentActivityId + " , activityType:" + activityType);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/attachments/upload", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ApiOperation(value = "(権限制御あり)添付ファイルのアップロード", notes = "AttachmentFormの添付ファイルをDB/サーバ上に登録")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "処理成功", response = ResponseError.class),
			@ApiResponse(code = 202, message = "登録・更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public ResponseEntityForm uploadAttachmentFile(@ApiParam(required = true, value = "添付ファイルフォーム")@ModelAttribute AttachmentForm attachmentForm, @ApiParam(hidden = true)Errors erros,@CookieValue(value = "token", required = false) String token) {

		LOGGER.info("添付ファイルのアップロード開始");
		if (erros.hasErrors() || attachmentForm.getActivityId() == null) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		String fileName = attachmentForm.getAttachmentFileName();
		int point = fileName.lastIndexOf(".");
		if (!"pdf".equals(fileName.substring(point + 1)) 
			&& !"PDF".equals(fileName.substring(point + 1)) 
			&& !"jpg".equals(fileName.substring(point + 1)) 
			&& !"jpeg".equals(fileName.substring(point + 1))
			&& !"JPG".equals(fileName.substring(point + 1))
			&& !"JPEG".equals(fileName.substring(point + 1)) 
			&& !"png".equals(fileName.substring(point + 1))
			&& !"PNG".equals(fileName.substring(point + 1)) 
			&& !"tif".equals(fileName.substring(point + 1)) 
			&& !"tiff".equals(fileName.substring(point + 1))
			&& !"TIF".equals(fileName.substring(point + 1))
			&& !"TIFF".equals(fileName.substring(point + 1))) {
			LOGGER.warn("リクエストエラー(形式不整合)");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		//偽装対策用にMultipartFileからのファイル名もチェックする
		fileName = attachmentForm.getUploadFile().getOriginalFilename();
		point = fileName.lastIndexOf(".");
		if (!"pdf".equals(fileName.substring(point + 1)) 
			&& !"PDF".equals(fileName.substring(point + 1)) 
			&& !"jpg".equals(fileName.substring(point + 1)) 
			&& !"jpeg".equals(fileName.substring(point + 1))
			&& !"JPG".equals(fileName.substring(point + 1))
			&& !"JPEG".equals(fileName.substring(point + 1)) 
			&& !"png".equals(fileName.substring(point + 1))
			&& !"PNG".equals(fileName.substring(point + 1)) 
			&& !"tif".equals(fileName.substring(point + 1)) 
			&& !"tiff".equals(fileName.substring(point + 1))
			&& !"TIF".equals(fileName.substring(point + 1))
			&& !"TIFF".equals(fileName.substring(point + 1))) {
			LOGGER.warn("リクエストエラー(形式不整合)");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		Optional<Attachment> attachmentOpt = attachmentsService.findByActivityIdAndAttachmentFileName(
				attachmentForm.getActivityId(), attachmentForm.getAttachmentFileName());
		Optional<Activity> activityOpt = activityService.findByActivityIdForEntity(attachmentForm.getActivityId());

		if (activityOpt.isPresent()) {
			//更新権限チェックを実施
			if(!AuthUtil.postUpdateAuthorityCheck(token, activityOpt.get().getPostUserId(), activityOpt.get().getPublishFlag())) {
				LOGGER.error("権限エラー");
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			attachmentOpt = attachmentsService.upload(attachmentForm);
			if (attachmentOpt.isPresent()) {
				attachmentForm.setId(attachmentOpt.get().getId());
			}
			ResponseEntityForm responseEntityForm = new ResponseEntityForm(HttpStatus.CREATED.value(),
					"Attachment File registration successful.");
			return responseEntityForm;
		} else {
			LOGGER.info("アップロード対象の活動情報が存在しない　activityId: "+attachmentForm.getActivityId());
			throw new ResponseStatusException(HttpStatus.ACCEPTED);
		}
	}

	@RequestMapping(value = "/attachments/{activityId}/{fileName}", method = RequestMethod.GET)
	@ApiOperation(value = "(権限制御あり)添付ファイルを表示", notes = "activityIdとfileNameで添付ファイルを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 202, message = "投稿が存在しない", response = ResponseError.class),
			@ApiResponse(code = 403, message = "認可エラー", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public HttpEntity<byte[]> getAttachmentFile(@ApiParam(required = true, value = "活動ID")@PathVariable("activityId") Integer activityId,
			@ApiParam(required = true, value = "ファイル名")@PathVariable("fileName") String fileName,@CookieValue(value = "token", required = false) String token) {
		//閲覧権限チェックを実施
		Optional<Activity> activityOpt = activityService.findByActivityIdForEntity(activityId);
		if (!activityOpt.isPresent()) {
			throw new ResponseStatusException(HttpStatus.ACCEPTED);
		}
		if(!AuthUtil.postViewAuthorityCheck(token, activityOpt.get().getPostUserId(), activityOpt.get().getPublishFlag())) {
			LOGGER.error("権限エラー");
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			// 絶対ファイルパス
			String absoluteFilePath = fileRootPath + activityId + "/" + fileName;
			Path filePath = Paths.get(absoluteFilePath);
			if (!Files.exists(filePath)) {
				// ファイルが存在しない
				LOGGER.warn("ファイルが存在しない");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			// リソースファイルを読み込み
			File file = new File(absoluteFilePath);
			InputStream is = new FileInputStream(file);
			// byteへ変換
			byte[] data = IOUtils.toByteArray(is);
			// レスポンスデータとして返却
			is = new BufferedInputStream(new ByteArrayInputStream(data));
			String mimeType = null;
			int point = fileName.lastIndexOf(".");
			if ("pdf".equals(fileName.substring(point + 1)) || "PDF".equals(fileName.substring(point + 1))) {
				mimeType = "application/pdf";
			} else if ("jpg".equals(fileName.substring(point + 1)) || "jpeg".equals(fileName.substring(point + 1))
					|| "JPG".equals(fileName.substring(point + 1))
					|| "JPEG".equals(fileName.substring(point + 1))) {
				mimeType = "image/jpeg";
			} else if ("png".equals(fileName.substring(point + 1))
					|| "PNG".equals(fileName.substring(point + 1))) {
				mimeType = "image/png";
			} else if ("tif".equals(fileName.substring(point + 1)) || "tiff".equals(fileName.substring(point + 1))
					|| "TIF".equals(fileName.substring(point + 1))
					|| "TIFF".equals(fileName.substring(point + 1))) {
				mimeType = "image/tiff";
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", mimeType);
			headers.setContentLength(data.length);
			LOGGER.info("添付ファイルの取得:" + absoluteFilePath);
			return new HttpEntity<byte[]>(data, headers);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("添付ファイルの取得に失敗 activityId： " + activityId + " , fileName: "+ fileName);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@RequestMapping(value = "/billboard/iconImage", method = RequestMethod.GET)
	@ApiOperation(value = "エリマネ・イベント活動のbillboard用アイコンを取得", notes = "参加者数に応じたアイコンを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "リクエスト不正", response = ResponseError.class),
			@ApiResponse(code = 404, message = "対象データが存在しない", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理エラー", response = ResponseError.class) })
	public HttpEntity<byte[]> getIconImage(@ApiParam(required = true, value = "参加者数")@RequestParam("participantsCount") Integer participantsCount) {
		try {
			// アイコンパス
			String absoluteIconPath = iconRootPath + "sample-icon.png";
			// json定義のアイコン設定から判定
			JSONArray jsonArray = new JSONArray(iconSettings);
			int len = jsonArray.length();
			for (int i=0;i<len;i++){ 
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				int min = jsonObject.getInt("min");
				int max = jsonObject.getInt("max");
				String iconPath = jsonObject.getString("iconPath");
				if(min <= participantsCount.intValue() && participantsCount.intValue() <=max) {
					absoluteIconPath = iconRootPath + iconPath;
				}
		    } 
			LOGGER.debug(absoluteIconPath);
			Path filePath = Paths.get(absoluteIconPath);
			if (!Files.exists(filePath)) {
				// ファイルが存在しない
				LOGGER.warn("ファイルが存在しない");
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			// リソースファイルを読み込み
			File file = new File(absoluteIconPath);
			InputStream is = new FileInputStream(file);
			// byteへ変換
			byte[] data = IOUtils.toByteArray(is);
			// レスポンスデータとして返却
			is = new BufferedInputStream(new ByteArrayInputStream(data));
			String mimeType = "image/png";
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", mimeType);
			headers.setContentLength(data.length);
			LOGGER.info("添付ファイルの取得:" + absoluteIconPath);
			return new HttpEntity<byte[]>(data, headers);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus());
		} catch (Exception e) {
			LOGGER.error("添付ファイルの取得に失敗 participantsCount： " + participantsCount);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
