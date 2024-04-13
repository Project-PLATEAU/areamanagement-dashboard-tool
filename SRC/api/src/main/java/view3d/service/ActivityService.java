package view3d.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.dao.ActivityDao;
import view3d.dao.TatemonoDAO;
import view3d.entity.Activity;
import view3d.entity.ActivityExtra;
import view3d.entity.Attachment;
import view3d.form.ActivityForm;
import view3d.form.AttachmentForm;
import view3d.form.DeleteActivityForm;
import view3d.form.PostSearchForm;
import view3d.repository.ActivityExtraRepository;
import view3d.repository.ActivityRepository;
import view3d.repository.jdbc.ActivityJdbc;

@Service
public class ActivityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);

	@Autowired
	ActivityRepository activityRepository;
	
	@Autowired
	ActivityExtraRepository activityExtraRepository;

	@Autowired
	ActivityJdbc activityJdbc;

	@Autowired
	AttachmentsService attachmentsService;
	
	@Autowired
	TatemonoDAO tatemonoDAO;
	
	@Value("${app.tatemono.default.height}")
	Double defaultHeight;
	
	@Value("${app.tatemono.flag}")
	boolean tatemonoFlag;
	
	/** Entityマネージャファクトリ */
	@Autowired
	protected EntityManagerFactory emf;
	
	@Value("${app.api.rootpath}")
	protected String apiRootPath;

	/**
	 * 活動情報の取得
	 * 
	 * @param activityId 活動id
	 * @return activity 活動エンティティ
	 */
	public Optional<Activity> findByActivityIdForEntity(Integer activityId) {
		return activityRepository.findById(activityId);
	}

	/**
	 * 活動情報の履歴情報取得
	 * 
	 * @param parentActivityId   活動id
	 * @param activityType 活動type
	 * @return List<Activity> 活動エンティティ一覧
	 */
	public List<Activity> findByParentActivityIdAndActivityTypeForHistory(Integer parentActivityId, Integer activityType) {
		LOGGER.info("活動情報の履歴情報取得 parentActivityId: " + parentActivityId
		+ " , activityType: " + activityType);
		return activityRepository.findByParentActivityIdAndActivityType(parentActivityId, activityType);
	}

	/**
	 * 活動情報の取得
	 * 
	 * @param activityId 活動id
	 * @return activityForm 活動フォーム
	 */
	public ActivityForm findByActivityId(Integer activityId) {
		Optional<Activity> activityOpt = activityRepository.findByActivityId(activityId);
		ActivityForm activityForm = new ActivityForm();
		if (activityOpt.isPresent()) {
			Activity activity = activityOpt.get();
			List<Attachment> attachmentList = attachmentsService.findByActivityId(activity.getActivityId());
			List<AttachmentForm> attachmentFormList = new ArrayList<>();
			for (Attachment attachment : attachmentList) {
				String fileUrl = apiRootPath + attachment.getActivityId() + "/" + attachment.getAttachmentFileName();
				attachmentFormList.add(new AttachmentForm(attachment.getId(), attachment.getActivityId(), 0, null, fileUrl, null));
			}
			activityForm.setActivityId(activity.getActivityId());
			activityForm.setGeom(activity.getGeom());
			activityForm.setStartDateAndTime(activity.getStartDateAndTime());
			activityForm.setEndDateAndTime(activity.getEndDateAndTime());
			activityForm.setActivityType(activity.getActivityType());
			activityForm.setActivityTypeName(activity.getActivityTypeObj().getTypeName());
			activityForm.setGroupType(activity.getGroupType());
			activityForm.setGroupTypeName(activity.getGroupTypeObj().getTypeName());
			activityForm.setActivityName(activity.getActivityName());
			activityForm.setActivityPlace(activity.getActivityPlace());
			activityForm.setActivityContent(activity.getActivityContent());
			activityForm.setParticipantCount(activity.getParticipantsCount());
			activityForm.setRemarks(activity.getRemarks());
			activityForm.setParentActivityId(activity.getParentActivityId());
			activityForm.setAttachmentFormList(attachmentFormList);
			activityForm.setPostUserId(activity.getPostUserId());
			activityForm.setPublishFlag(activity.getPublishFlag());
			LOGGER.info("活動情報の取得 activityId: " + activityId);
		}else {
			LOGGER.info("活動情報の取得に失敗 activityId: " + activityId);
		}
		return activityForm;
	}
	
	/**
	 * 活動情報の取得
	 * 
	 * @param activityType 活動タイプ
	 * @return activityFormList 活動フォームリスト
	 */
	public List<ActivityForm> findByActivityType(Integer activityType) {
		LOGGER.info("活動情報の取得 activityType: " + activityType);
		List<ActivityExtra> activityList = activityExtraRepository.findActivityType(activityType);
		List<ActivityForm> activityFormList = new ArrayList<>();
		for(ActivityExtra activity:activityList) {
			ActivityForm activityForm = new ActivityForm();
			if (activity != null && activity.getActivityId() != null ) {
				List<Attachment> attachmentList = attachmentsService.findByActivityId(activity.getActivityId());
				List<AttachmentForm> attachmentFormList = new ArrayList<>();
				for (Attachment attachment : attachmentList) {
					String fileUrl = apiRootPath + attachment.getActivityId() + "/" + attachment.getAttachmentFileName();
					attachmentFormList.add(new AttachmentForm(attachment.getId(), attachment.getActivityId(), 0, null, fileUrl, null));
				}
				activityForm.setActivityId(activity.getActivityId());
				activityForm.setLongitude(activity.getLongitude());
				activityForm.setLatitude(activity.getLatitude());
				activityForm.setStartDateAndTime(activity.getStartDateAndTime());
				activityForm.setEndDateAndTime(activity.getEndDateAndTime());
				activityForm.setActivityType(activity.getActivityType());
				activityForm.setActivityTypeName(activity.getActivityTypeObj().getTypeName());
				activityForm.setGroupType(activity.getGroupType());
				activityForm.setGroupTypeName(activity.getGroupTypeObj().getTypeName());
				activityForm.setActivityName(activity.getActivityName());
				activityForm.setActivityPlace(activity.getActivityPlace());
				activityForm.setActivityContent(activity.getActivityContent());
				activityForm.setParticipantCount(activity.getParticipantsCount());
				activityForm.setRemarks(activity.getRemarks());
				activityForm.setParentActivityId(activity.getParentActivityId());
				activityForm.setAttachmentFormList(attachmentFormList);
				activityForm.setPostUserId(activity.getPostUserId());
				activityForm.setInsertTime(activity.getInsertTime());
				activityForm.setPublishFlag(activity.getPublishFlag());
			}else {
				LOGGER.info("活動情報の取得に失敗 activityId: " + activity.getActivityId());
			}
			activityFormList.add(activityForm);
		}
		LOGGER.info("活動情報の取得終了 activityType: " + activityType);
		return activityFormList;
	}
	
	/**
	 * 活動情報の取得(投稿日時での検索あり)
	 * 
	 * @param activityType 活動タイプ
	 * @param postSearchForm 投稿検索フォーム
	 * @return activityFormList 活動フォームリスト
	 */
	public List<ActivityForm> findByActivityTypeAndSearchData(Integer activityType,PostSearchForm postSearchForm) {
		LOGGER.info("活動情報の取得 activityType: " + activityType);
		String orderMode = "desc";
		if("0".equals(postSearchForm.getSortFlag())) {
			orderMode = "asc";
		}
		ActivityDao activityDao = new ActivityDao(emf);
		List<ActivityExtra> activityList = activityDao.findActivityTypeAndSearchData(activityType,postSearchForm,orderMode);
		List<ActivityForm> activityFormList = new ArrayList<>();
		for(ActivityExtra activity:activityList) {
			ActivityForm activityForm = new ActivityForm();
			if (activity != null && activity.getActivityId() != null ) {
				List<Attachment> attachmentList = attachmentsService.findByActivityId(activity.getActivityId());
				List<AttachmentForm> attachmentFormList = new ArrayList<>();
				for (Attachment attachment : attachmentList) {
					String fileUrl = apiRootPath + attachment.getActivityId() + "/" + attachment.getAttachmentFileName();
					attachmentFormList.add(new AttachmentForm(attachment.getId(), attachment.getActivityId(), 0, null, fileUrl, null));
				}
				activityForm.setActivityId(activity.getActivityId());
				activityForm.setLongitude(activity.getLongitude());
				activityForm.setLatitude(activity.getLatitude());
				activityForm.setStartDateAndTime(activity.getStartDateAndTime());
				activityForm.setEndDateAndTime(activity.getEndDateAndTime());
				activityForm.setActivityType(activity.getActivityType());
				activityForm.setActivityTypeName(activity.getActivityTypeObj().getTypeName());
				activityForm.setGroupType(activity.getGroupType());
				activityForm.setGroupTypeName(activity.getGroupTypeObj().getTypeName());
				activityForm.setActivityName(activity.getActivityName());
				activityForm.setActivityPlace(activity.getActivityPlace());
				activityForm.setActivityContent(activity.getActivityContent());
				activityForm.setParticipantCount(activity.getParticipantsCount());
				activityForm.setRemarks(activity.getRemarks());
				activityForm.setParentActivityId(activity.getParentActivityId());
				activityForm.setAttachmentFormList(attachmentFormList);
				activityForm.setPostUserId(activity.getPostUserId());
				activityForm.setInsertTime(activity.getInsertTime());
				activityForm.setPublishFlag(activity.getPublishFlag());
			}else {
				LOGGER.info("活動情報の取得に失敗 activityId: " + activity.getActivityId());
			}
			activityFormList.add(activityForm);
		}
		LOGGER.info("活動情報の取得終了 activityType: " + activityType);
		return activityFormList;
	}
	
	/**
	 * 活動情報の公開設定更新
	 * 
	 * @param activityFormList 活動フォームリスト
	 * @return result true:更新成功 false:更新失敗
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updatePublish(List<ActivityForm> activityFormList) {
		boolean result = true;
		for(ActivityForm activityForm : activityFormList) {
			Optional<Activity> activityOpt = activityRepository.findById(activityForm.getActivityId());
			if(activityOpt.isPresent()) {
				Activity activity = activityOpt.get();
				activity.setPublishFlag(activityForm.getPublishFlag());
				activityRepository.save(activity);
			}else {
				result = false;
			}
		}
		return result;
	}

	/**
	 * 活動情報の登録
	 * 
	 * @param activityForm 活動フォーム
	 * @return activityForm 登録後の活動フォーム
	 */
	@Transactional
	public ActivityForm insert(ActivityForm activityForm) {
		// 活動情報のactivityIdを初期化
		Integer activityId = 0;
		// 地点への追加登録であれば、対象地点のgeomを使用 それ以外はクリック時の経度と緯度からgeomを生成しinsert処理を行う
		if (activityForm.getGeom() != null && !"".equals(activityForm.getGeom())) {
			// insertした活動情報のactivityIdを取得
			activityId = activityJdbc.quoteInsert(activityForm);
		} else {
			// 経度と緯度の整形
			String longLat = activityForm.getLongitude() + " " + activityForm.getLatitude();
			// 建物モデルから高さ情報を取得する
			double height = defaultHeight;
			try {
				if(tatemonoFlag) {
					height = tatemonoDAO.getTatemonoHeight(longLat);
				}
			} catch (Exception e) {}
			longLat = longLat + " " + height;
			// insertした活動情報のactivityIdを取得
			activityId = activityJdbc.insert(activityForm, longLat);
		}
		LOGGER.info("活動情報を登録 activityId: " + activityId);
		// 返却用DTOにactivityIdをセット
		activityForm.setActivityId(activityId);
		// insertした活動情報を取得し親のactivityIdをセットする
		Optional<Activity> activityOpt = findByActivityIdForEntity(activityId);
		if (activityOpt.isPresent()) {
			Activity activity = activityOpt.get();
			// 親のactivityIdが存在しない場合は自身のactivityIdをセットし更新
			if (activity.getParentActivityId() == 0 || activity.getParentActivityId() == null) {
				activity.setParentActivityId(activityId);
				activityRepository.save(activity);
				activityForm.setParentActivityId(activityId);
				LOGGER.info("parentActivityIdの更新 activityId: " + activity.getActivityId() + " , parentActivityId: "
						+ activity.getParentActivityId());
			}
		}
		return activityForm;
	}

	/**
	 * 活動情報の更新
	 * 
	 * @param activity     活動エンティティ
	 * @param activityForm 活動フォーム
	 */
	@Transactional
	public void update(Activity activity, ActivityForm activityForm) {
		activity.setUpdateTime(LocalDateTime.now());
		activity.setStartDateAndTime(activityForm.getStartDateAndTime());
		activity.setEndDateAndTime(activityForm.getEndDateAndTime());
		activity.setActivityType(activityForm.getActivityType());
		activity.setGroupType(activityForm.getGroupType());
		activity.setActivityName(activityForm.getActivityName());
		activity.setActivityPlace(activityForm.getActivityPlace());
		activity.setActivityContent(activityForm.getActivityContent());
		activity.setParticipantsCount(activityForm.getParticipantCount());
		activity.setRemarks(activityForm.getRemarks());
		activity.setParentActivityId(activityForm.getParentActivityId());
		activity.setPublishFlag(activityForm.getPublishFlag());
		//投稿者の更新は行わないようにする
		//activity.setPostUserId(activityForm.getPostUserId());
		attachmentsService.deleteByIdDbOnly(activityForm.getAttachmentFormDeleteList());
		activityRepository.save(activity);
		LOGGER.info("活動情報を更新 activityId: " + activity.getActivityId());
	}

	/**
	 * 活動情報の削除
	 * 
	 * @param activityForm 活動フォーム
	 * @return newParentActivityId 削除後の親活動ID
	 */
	@Transactional
	public Integer delete(DeleteActivityForm activityForm) {
		// 添付ファイルの削除
		List<Attachment> attachmentDeleteList = attachmentsService.findByActivityId(activityForm.getActivityId());
		List<AttachmentForm> attachmentFormDeleteList = new ArrayList<>();
		if (attachmentDeleteList != null) {
			for (Attachment attachment : attachmentDeleteList) {
				attachmentFormDeleteList.add(new AttachmentForm(attachment.getId(), attachment.getActivityId(), 0, null,
						attachment.getAttachmentFileName(), null));
			}
		}
		attachmentsService.deleteByIdDbOnly(attachmentFormDeleteList);
		// 活動情報の削除
		int deleteCount = activityRepository.deleteByActivityId(activityForm.getActivityId());
		LOGGER.info("活動情報の削除 activityId: " + activityForm.getActivityId());
		Integer newParentActivityId = activityForm.getParentActivityId();
		// 親の活動情報である場合、子の活動情報のparentActivityIdを更新する
		if (deleteCount > 0
				&& activityForm.getParentActivityId().intValue() == activityForm.getActivityId().intValue()) {
			List<Activity> activityList = activityRepository.findByNotCurrentActivityIdParentActivityId(
					activityForm.getActivityId(), activityForm.getParentActivityId());
			if (activityList != null && activityList.size() > 0) {
				if (activityList.get(0) != null) {
					newParentActivityId = activityList.get(0).getActivityId();
					for (Activity activity : activityList) {
						activity.setParentActivityId(newParentActivityId);
						activityRepository.save(activity);
						LOGGER.info("parentActivityIDの更新 activityId: " + activity.getActivityId()
								+ " , parentActivityId: " + activity.getParentActivityId());
					}
				}
			}
		}
		// 削除後の親活動IDを返す
		return newParentActivityId;
	}
}
