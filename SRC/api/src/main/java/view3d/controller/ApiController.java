package view3d.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import view3d.entity.ResponseError;
import view3d.form.ActivityForm;
import view3d.form.ActivityListForm;
import view3d.form.AttachmentForm;
import view3d.form.DeleteActivityForm;
import view3d.form.ResponseEntityForm;
import view3d.form.TypeForm;
import view3d.form.TypeListForm;
import view3d.service.ActivityService;
import view3d.service.ActivityTypeService;
import view3d.service.AttachmentsService;
import view3d.service.GroupTypeService;

@RestController
@RequestMapping("/activity")
public class ApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

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

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ApiOperation(value = "活動情報を取得", notes = "activityIdに紐づく活動情報を取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 400, message = "activityIdが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public ActivityForm getActivity(@ApiParam(required = true, value = "活動ID")@RequestParam("activityId") Integer activityId) {
		try {
			ActivityForm activityForm = activityService.findByActivityId(activityId);
			return activityForm;
		} catch (Exception e) {
			LOGGER.error("活動情報の取得に失敗 activityId： " + activityId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "活動情報を登録・更新", notes = "activityIdに紐づく活動情報が存在しない場合は登録、activityIdに紐づく活動情報が存在する場合は更新")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "ActivityFormに対応しないリクエストの場合", response = ResponseError.class),
			@ApiResponse(code = 202, message = "更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public ActivityForm registerActivity(@ApiParam(required = true, value = "活動フォーム")@RequestBody @Validated ActivityForm activityForm,@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("活動情報の登録・更新開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			Activity activity = new Activity();
			Optional<Integer> idOps = Optional.ofNullable(activityForm.getActivityId());
			if (idOps.isPresent() && idOps.get() != 0) {
				Optional<Activity> activityOpt = activityService.findByActivityIdForEntity(idOps.get());
				if (activityOpt.isPresent()) {
					activity = activityOpt.get();
					activityService.update(activity, activityForm);
				} else {
					throw new ResponseStatusException(HttpStatus.ACCEPTED);
				}
			} else {
				activityForm = activityService.insert(activityForm);
			}
			LOGGER.info("活動情報の登録・更新終了");
			return activityForm;
		} catch (Exception e) {
			LOGGER.error("活動情報の登録・更新に失敗 activityId： " + activityForm.getActivityId());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ApiOperation(value = "活動情報を削除", notes = "活動情報を削除し新たなparentActivityIdを取得")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "ActivityFormに対応しないリクエストの場合", response = ResponseError.class),
			@ApiResponse(code = 202, message = "更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public DeleteActivityForm deleteActivity(@ApiParam(required = true, value = "活動削除フォーム")@RequestBody @Validated DeleteActivityForm activityForm,@ApiParam(hidden = true)Errors erros) {
		try {
			LOGGER.info("活動情報の削除開始");
			if (erros.hasErrors()) {
				LOGGER.warn("リクエストエラー");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			if (activityForm.getActivityId() > 0 && activityForm.getParentActivityId() > 0) {
				Integer newParentActivityId = activityService.delete(activityForm);
				LOGGER.info("活動情報の削除終了");
				return new DeleteActivityForm(activityForm.getActivityId(), newParentActivityId);
			} else {
				throw new ResponseStatusException(HttpStatus.ACCEPTED);
			}
		} catch (Exception e) {
			LOGGER.error("活動情報の削除に失敗 activityId： " + activityForm.getActivityId());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/type", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "活動タイプ・グループタイプを取得", notes = "登録済みの活動タイプ・グループタイプを取得")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
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
	@ApiOperation(value = "活動情報の履歴を取得", notes = "parentActivityIdとactivityTypeに紐づくactivityIdと開始日時一覧を取得")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "parentActivityIdが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public ActivityListForm getActivityHistory(@ApiParam(required = true, value = "親活動ID")@RequestParam("parentActivityId") Integer parentActivityId,
			@ApiParam(required = true, value = "活動タイプ")@RequestParam("activityType") Integer activityType) {
		try {
			ActivityListForm activityIdListForm = new ActivityListForm();
			List<Activity> activityList = activityService.findByParentActivityIdAndActivityTypeForHistory(parentActivityId,
					activityType);
			List<ActivityForm> activityFormList = new ArrayList<>();
			for (Activity activity : activityList) {
				ActivityForm activityForm = new ActivityForm();
				activityForm.setActivityId(activity.getActivityId());
				activityForm.setStartDateAndTime(activity.getStartDateAndTime());
				activityFormList.add(activityForm);
			}
			activityIdListForm.setActivityFormList(activityFormList);
			return activityIdListForm;
		} catch (Exception e) {
			LOGGER.error("活動情報の履歴情報取得に失敗 parentActivityId： " + parentActivityId + " , activityType:" + activityType);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/attachments/upload", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ApiOperation(value = "添付ファイルのアップロード", notes = "AttachmentFormの添付ファイルをDB/サーバ上に登録")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "AttachmentFormに対応しないリクエストの場合", response = ResponseError.class),
			@ApiResponse(code = 202, message = "登録・更新対象の活動情報が存在しなかった場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public ResponseEntityForm uploadAttachmentFile(@ApiParam(required = true, value = "添付ファイルフォーム")@ModelAttribute AttachmentForm attachmentForm, @ApiParam(hidden = true)Errors erros) {

		LOGGER.info("添付ファイルのアップロード開始");
		if (erros.hasErrors()) {
			LOGGER.warn("リクエストエラー");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		Optional<Attachment> attachmentOpt = attachmentsService.findByActivityIdAndAttachmentFileName(
				attachmentForm.getActivityId(), attachmentForm.getAttachmentFileName());
		Optional<Activity> activityOpt = activityService.findByActivityIdForEntity(attachmentForm.getActivityId());

		if (activityOpt.isPresent()) {
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
	@ApiOperation(value = "添付ファイルを表示", notes = "activityIdとfileNameで添付ファイルを取得")
	@ResponseBody
	@ApiResponses(value = { @ApiResponse(code = 202, message = "添付ファイルが存在しない場合", response = ResponseError.class),
			@ApiResponse(code = 400, message = "activityId,fileNameが不正な場合", response = ResponseError.class),
			@ApiResponse(code = 500, message = "処理時にエラーが発生した場合", response = ResponseError.class) })
	public HttpEntity<byte[]> getAttachmentFile(@ApiParam(required = true, value = "活動ID")@PathVariable("activityId") Integer activityId,
			@ApiParam(required = true, value = "ファイル名")@PathVariable("fileName") String fileName) {
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
			String mimeType = URLConnection.guessContentTypeFromStream(is);
			if (mimeType == null) {
				int point = fileName.lastIndexOf(".");
				String sp = fileName.substring(point + 1);
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
				} else {
					mimeType = "application/octet-stream";
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", mimeType);
			headers.setContentLength(data.length);
			LOGGER.info("添付ファイルの取得:" + absoluteFilePath);
			return new HttpEntity<byte[]>(data, headers);
		} catch (Exception e) {
			LOGGER.error("添付ファイルの取得に失敗 activityId： " + activityId + " , fileName: "+ fileName);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
