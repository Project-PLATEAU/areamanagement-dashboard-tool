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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import view3d.dao.ActivityDao;
import view3d.dao.PostLayerDao;
import view3d.dao.TatemonoDAO;
import view3d.entity.Activity;
import view3d.entity.Layer;
import view3d.entity.PostLayerAttribute;
import view3d.entity.PostLayerAttributePK;
import view3d.entity.PostLayerFeature;
import view3d.entity.PostLayerFeatureExtra;
import view3d.entity.ThemeLayer;
import view3d.entity.ThemeLayerPK;
import view3d.form.ActivityForm;
import view3d.form.DeletePostLayerForm;
import view3d.form.PostLayerAttributeForm;
import view3d.form.PostLayerFeatureForm;
import view3d.form.PostSearchForm;
import view3d.form.ThemeLayerForm;
import view3d.repository.PostLayerAttributeRepository;
import view3d.repository.PostLayerFeatureExtraRepository;
import view3d.repository.PostLayerFeatureRepository;
import view3d.repository.jdbc.PostLayerJdbc;


@Service
public class PostLayerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostLayerService.class);
	
	/** Entityマネージャファクトリ */
	@Autowired
	protected EntityManagerFactory emf;
	
	@Autowired
	PostLayerAttributeRepository postLayerAttributeRepository;
	
	@Autowired
	PostLayerFeatureRepository postLayerFeatureRepository;
	
	@Autowired
	PostLayerFeatureExtraRepository postLayerFeatureExtraRepository;

	@Autowired
	PostLayerJdbc postLayerJdbc;
	
	@Autowired
	TatemonoDAO tatemonoDAO;
	
	@Value("${app.tatemono.default.height}")
	Double defaultHeight;
	
	@Value("${app.tatemono.flag}")
	boolean tatemonoFlag;

	@Value("${app.postLayer.rootpath}")
	protected String apiRootPath;

	/**
	 * 投稿レイヤ情報の取得
	 * 
	 * @param featureId フィーチャID
	 * @return postLayerFeatureForm
	 */
	public PostLayerFeatureForm getPostLayer(Integer featureId) {
		LOGGER.info("投稿レイヤ情報の取得開始 featureId:" + featureId);
		final Optional<PostLayerFeature> postLayerOpt = postLayerFeatureRepository.findById(featureId);
		PostLayerFeatureForm postLayerFeatureForm = new PostLayerFeatureForm();
		if ( postLayerOpt.isPresent()) {
			PostLayerFeature postLayer =  postLayerOpt.get();
			postLayerFeatureForm.setFeatureId(postLayer.getFeatureId());
			postLayerFeatureForm.setLayerId(postLayer.getLayerId());
			postLayerFeatureForm.setGeom(postLayer.getGeometry());
			postLayerFeatureForm.setPublishFlag(postLayer.getPublishFlag());
			postLayerFeatureForm.setPostUserId(postLayer.getPostUserId());
			postLayerFeatureForm.setParentFeatureId(postLayer.getParentFeatureId());
			postLayerFeatureForm.setPostDatetime(postLayer.getPostDatetime());
			postLayerFeatureForm.setItem1(postLayer.getItem1());
			if(checkAttachmentFile(featureId, postLayer.getItem1()) != null) postLayerFeatureForm.setItem1(checkAttachmentFile(featureId, postLayer.getItem1()));
			postLayerFeatureForm.setItem2(postLayer.getItem2());
			if(checkAttachmentFile(featureId, postLayer.getItem2()) != null) postLayerFeatureForm.setItem2(checkAttachmentFile(featureId, postLayer.getItem2()));
			postLayerFeatureForm.setItem3(postLayer.getItem3());
			if(checkAttachmentFile(featureId, postLayer.getItem3()) != null) postLayerFeatureForm.setItem3(checkAttachmentFile(featureId, postLayer.getItem3()));
			postLayerFeatureForm.setItem4(postLayer.getItem4());
			if(checkAttachmentFile(featureId, postLayer.getItem4()) != null) postLayerFeatureForm.setItem4(checkAttachmentFile(featureId, postLayer.getItem4()));
			postLayerFeatureForm.setItem5(postLayer.getItem5());
			if(checkAttachmentFile(featureId, postLayer.getItem5()) != null) postLayerFeatureForm.setItem5(checkAttachmentFile(featureId, postLayer.getItem5()));
			postLayerFeatureForm.setItem6(postLayer.getItem6());
			if(checkAttachmentFile(featureId, postLayer.getItem6()) != null) postLayerFeatureForm.setItem6(checkAttachmentFile(featureId, postLayer.getItem6()));
			postLayerFeatureForm.setItem7(postLayer.getItem7());
			if(checkAttachmentFile(featureId, postLayer.getItem7()) != null) postLayerFeatureForm.setItem7(checkAttachmentFile(featureId, postLayer.getItem7()));
			postLayerFeatureForm.setItem8(postLayer.getItem8());
			if(checkAttachmentFile(featureId, postLayer.getItem8()) != null) postLayerFeatureForm.setItem8(checkAttachmentFile(featureId, postLayer.getItem8()));
			postLayerFeatureForm.setItem9(postLayer.getItem9());
			if(checkAttachmentFile(featureId, postLayer.getItem9()) != null) postLayerFeatureForm.setItem9(checkAttachmentFile(featureId, postLayer.getItem9()));
			postLayerFeatureForm.setItem10(postLayer.getItem10());
			if(checkAttachmentFile(featureId, postLayer.getItem10()) != null) postLayerFeatureForm.setItem10(checkAttachmentFile(featureId, postLayer.getItem10()));
			LOGGER.info("投稿レイヤ情報の取得終了 featureId:" + featureId);
		}else {
			LOGGER.info("投稿レイヤ情報の取得に失敗 featureId: " + featureId);
		}
		return postLayerFeatureForm;
	}
	
	/**
	 * 投稿レイヤ情報の取得
	 * 
	 * @param layerId レイヤID
	 * @return postLayerFeatureFormList 投稿レイヤFormリスト
	 */
	public List<PostLayerFeatureForm> getPostLayerListByLayerId(Integer layerId) {
		LOGGER.info("投稿レイヤ情報の取得開始 layerId:" + layerId);
		List<PostLayerFeatureForm> postLayerFeatureFormList = null;
		List<PostLayerFeatureExtra> postLayerFeatureList = postLayerFeatureExtraRepository.getByLayerId(layerId);
		if(postLayerFeatureList != null && postLayerFeatureList.size() > 0) {
			postLayerFeatureFormList = entityToFormForPostLayerFeature(postLayerFeatureList);
		}
		LOGGER.info("投稿レイヤ情報の取得終了 layerId:" + layerId);
		return postLayerFeatureFormList;
	}
	
	/**
	 * 投稿レイヤ情報の取得(投稿日時での検索あり)
	 * 
	 * @param layerId レイヤID
	 * @param postSearchForm 投稿検索フォーム
	 * @return postLayerFeatureFormList 投稿レイヤFormリスト
	 */
	public List<PostLayerFeatureForm> getPostLayerListByLayerIdAndSearchData(Integer layerId,PostSearchForm postSearchForm) {
		LOGGER.info("投稿レイヤ情報の取得開始 layerId:" + layerId);
		List<PostLayerFeatureForm> postLayerFeatureFormList = null;
		String orderMode = "desc";
		if("0".equals(postSearchForm.getSortFlag())) {
			orderMode = "asc";
		}
		PostLayerDao postLayerDao = new PostLayerDao(emf);
		List<PostLayerFeatureExtra> postLayerFeatureList = postLayerDao.findLayerIdAndSearchData(layerId,postSearchForm,orderMode);
		if(postLayerFeatureList != null && postLayerFeatureList.size() > 0) {
			postLayerFeatureFormList = entityToFormForPostLayerFeature(postLayerFeatureList);
		}
		LOGGER.info("投稿レイヤ情報の取得終了 layerId:" + layerId);
		return postLayerFeatureFormList;
	}
	
	/**
	 * 投稿レイヤ情報の取得(投稿日時での検索あり) パス省略のCSV用
	 * 
	 * @param layerId レイヤID
	 * @param postSearchForm 投稿検索フォーム
	 * @return postLayerFeatureFormList 投稿レイヤFormリスト
	 */
	public List<PostLayerFeatureForm> getPostLayerListByLayerIdAndSearchDataForCSV(Integer layerId,PostSearchForm postSearchForm) {
		LOGGER.info("投稿レイヤ情報の取得開始 layerId:" + layerId);
		List<PostLayerFeatureForm> postLayerFeatureFormList = null;
		String orderMode = "desc";
		if("0".equals(postSearchForm.getSortFlag())) {
			orderMode = "asc";
		}
		PostLayerDao postLayerDao = new PostLayerDao(emf);
		List<PostLayerFeatureExtra> postLayerFeatureList = postLayerDao.findLayerIdAndSearchData(layerId,postSearchForm,orderMode);
		if(postLayerFeatureList != null && postLayerFeatureList.size() > 0) {
			postLayerFeatureFormList = entityToFormForPostLayerFeature2(postLayerFeatureList);
		}
		LOGGER.info("投稿レイヤ情報の取得終了 layerId:" + layerId);
		return postLayerFeatureFormList;
	}
	
	/**
	 * 添付ファイルの確認
	 */
	private String checkAttachmentFile(Integer featureId, String item) {
		if(item.endsWith(".png") || item.endsWith(".jpg") || item.endsWith(".jpeg")) {
			String fileUrl = apiRootPath + "feature_" + Integer.toString(featureId) + "/" + item;
			return fileUrl;
		}else {
			return null;
		}
	}
	
	/**
	 * 投稿レイヤ情報の取得
	 * 
	 * @param themeId テーマID
	 * @return 
	 */
	public Layer getPostLayerInfo(Integer themeId) {
		LOGGER.info("投稿レイヤ属性の取得開始 themeId:" + themeId);
		try {
			PostLayerDao postLayerDao = new PostLayerDao(emf);
			final Layer postLayerInfo = postLayerDao.getPostLayerInfo(themeId);
			return postLayerInfo;
		} finally {
			LOGGER.info("投稿レイヤ属性の取得終了 themeId:" + themeId);
		}
	}
	
	/**
	 * 投稿レイヤ属性の取得
	 * 
	 * @param themeId テーマID
	 * @return themeLayerFormList テーマレイヤFormリスト
	 */
	public List<PostLayerAttribute> getPostLayerAttribute(Integer themeId) {
		LOGGER.info("投稿レイヤ属性の取得開始 themeId:" + themeId);
		try {
			PostLayerDao postLayerDao = new PostLayerDao(emf);
			final List<PostLayerAttribute> postLayerList = postLayerDao.getPostLayerAttribute(themeId);
			if (postLayerList.size() > 0) {
				return postLayerList;
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
		} finally {
			LOGGER.info("投稿レイヤ属性の取得終了 themeId:" + themeId);
		}
	}
	
	/**
	 * 投稿レイヤ属性の取得
	 * 
	 * @param layerId レイヤID
	 * @return postLayerAttributeFormList　投稿レイヤ属性Formリスト
	 */
	public List<PostLayerAttributeForm> getPostLayerAttributeByLayerId(Integer layerId) {
		LOGGER.info("投稿レイヤ属性の取得開始 layerId:" + layerId);
		try {
			List<PostLayerAttributeForm> postLayerAttributeFormList = null;
			final List<PostLayerAttribute> postLayerAttributeList = postLayerAttributeRepository.findByLayerId(layerId);
			if (postLayerAttributeList.size() > 0) {
				postLayerAttributeFormList = entityToFormForPostLayerAttribute(postLayerAttributeList);
			}
			return postLayerAttributeFormList;
		} finally {
			LOGGER.info("投稿レイヤ属性の取得終了  layerId:" + layerId);
		}
	}
	
	/**
	 * 投稿レイヤ属性の取得(表示順)
	 * 
	 * @param layerId レイヤID
	 * @return postLayerAttributeFormList　投稿レイヤ属性Formリスト
	 */
	public List<PostLayerAttributeForm> getPostLayerAttributeByLayerIdOrderByDispOrder(Integer layerId) {
		LOGGER.info("投稿レイヤ属性の取得開始 layerId:" + layerId);
		try {
			List<PostLayerAttributeForm> postLayerAttributeFormList = null;
			final List<PostLayerAttribute> postLayerAttributeList = postLayerAttributeRepository.findByLayerIdOrderByDispOrder(layerId);
			if (postLayerAttributeList.size() > 0) {
				postLayerAttributeFormList = entityToFormForPostLayerAttribute(postLayerAttributeList);
			}
			return postLayerAttributeFormList;
		} finally {
			LOGGER.info("投稿レイヤ属性の取得終了  layerId:" + layerId);
		}
	}

	/**
	 * 投稿情報の取得
	 * 
	 * @param featureId 活動id
	 * @return postLayerFeature 活動エンティティ
	 */
	public Optional<PostLayerFeature> findByFeatureIdForEntity(Integer featureId) {
		return postLayerFeatureRepository.findById(featureId);
	}

	/**
	 * 投稿レイヤフィーチャの登録
	 * 
	 * @param postLayerFeatureForm 投稿レイヤフィーチャフォーム
	 * @return postLayerFeatureForm 登録後の投稿レイヤフィーチャフォーム
	 */
	@Transactional
	public PostLayerFeatureForm insert(PostLayerFeatureForm postLayerFeatureForm) {
		// 活動情報のfeatureIdを初期化
		Integer featureId = 0;
		// 地点への追加登録であれば、対象地点のgeomを使用 それ以外はクリック時の経度と緯度からgeomを生成しinsert処理を行う
		if (postLayerFeatureForm.getGeom() != null && !"".equals(postLayerFeatureForm.getGeom())) {
			LOGGER.info("地点への追加登録 ");
			// insertした活動情報のfeatureIdを取得
			featureId = postLayerJdbc.quoteInsert(postLayerFeatureForm);
		} else {
			LOGGER.info("クリック時の経度と緯度からgeomを生成 ");
			// 経度と緯度の整形
			String longLat = postLayerFeatureForm.getLongitude() + " " + postLayerFeatureForm.getLatitude();
			// 建物モデルから高さ情報を取得する
			double height = defaultHeight;
			try {
				if(tatemonoFlag) {
					height = tatemonoDAO.getTatemonoHeight(longLat);
				}
			} catch (Exception e) {}
			longLat = longLat + " " + height;
			// insertした活動情報のfeatureIdを取得
			LOGGER.debug("" + postLayerFeatureForm);
			featureId = postLayerJdbc.insert(postLayerFeatureForm, longLat);
		}
		LOGGER.info("活動情報を登録 featureId: " + featureId);
		// 返却用DTOにfeatureIdをセット
		postLayerFeatureForm.setFeatureId(featureId);;
		// insertした活動情報を取得し親のfeatureIdをセットする
		Optional<PostLayerFeature> featureOpt = findByFeatureIdForEntity(featureId);
		if (featureOpt.isPresent()) {
			PostLayerFeature feature = featureOpt.get();
			
			// 親のfeatureIdが存在しない場合は自身のfeatureIdをセットし更新
			if (feature.getParentFeatureId() == 0 || feature.getParentFeatureId() == null) {
				feature.setParentFeatureId(featureId);
				postLayerFeatureRepository.save(feature);
				postLayerFeatureForm.setParentFeatureId(featureId);
				LOGGER.info("parentFeatureIdの更新 featureId: " + feature.getFeatureId() + " , parentFeatureId: "
						+ feature.getParentFeatureId());
			}
		}
		return postLayerFeatureForm;
	}
	

	/**
	 * 投稿レイヤ属性情報の更新
	 * 
	 * @param List<PostLayerAttributeForm> postLayerAttributeFormList
	 * @throws Exception 
	 */
	@Transactional
	public List<PostLayerAttributeForm> updateAttribute(List<PostLayerAttributeForm> postLayerAttributeFormList, boolean flag) throws Exception {
		LOGGER.info("レイヤ属性情報の更新開始");
		boolean check = false;
		Integer layerId = 0;
		if(postLayerAttributeFormList != null && postLayerAttributeFormList.size() > 0) {
			for(PostLayerAttributeForm postLayerAttributeForm: postLayerAttributeFormList) {
				layerId = postLayerAttributeForm.getLayerId();
				PostLayerAttributePK postLayerAttributePK = new PostLayerAttributePK();
				postLayerAttributePK.setLayerId(postLayerAttributeForm.getLayerId());
				postLayerAttributePK.setItemId(postLayerAttributeForm.getItemId());
				LOGGER.info("itemId:" + postLayerAttributePK.getItemId());
				Optional<PostLayerAttribute> postLayerAttributeOpt = postLayerAttributeRepository.findById(postLayerAttributePK);
					PostLayerAttribute postLayerAttribute = postLayerAttributeOpt.get();
					postLayerAttribute.setItemName(postLayerAttributeForm.getItemName());
					if(flag == false) {
						postLayerAttribute.setItemType(postLayerAttributeForm.getItemType());
					}
					postLayerAttribute.setDispOrder(postLayerAttributeForm.getDispOrder());
					//LOGGER.debug("" + themeLayer);
					PostLayerAttribute resultpostLayerAttribute = postLayerAttributeRepository.saveAndFlush(postLayerAttribute);
					if(resultpostLayerAttribute == null) {
						check = false;
					}else {
						check = true;
					}
					if(!check) {
						throw new Exception("processing error");
					}
			}
		}
		LOGGER.info("レイヤ属性情報の更新終了");
		List<PostLayerAttributeForm> result = null;
		final List<PostLayerAttribute> res = postLayerAttributeRepository.findByLayerId(layerId);
		if (res.size() > 0) {
			result = entityToFormForPostLayerAttribute(res);
		}
		return result;
	}
	
	/**
	 * 投稿レイヤフィーチャの更新
	 * 
	 * @param postLayerFeature     投稿レイヤフィーチャエンティティ
	 * @param postLayerFeatureForm 投稿レイヤフィーチャフォーム
	 */
	@Transactional
	public void updateFeature(PostLayerFeature postLayerFeature, PostLayerFeatureForm postLayerFeatureForm) {
		postLayerFeature.setPostDatetime(LocalDateTime.now());
		postLayerFeature.setPublishFlag(postLayerFeatureForm.getPublishFlag());
		//投稿者の更新は行わないようにする
		//postLayerFeature.setPostUserId(postLayerFeatureForm.getPostUserId());
		postLayerFeature.setParentFeatureId(postLayerFeatureForm.getParentFeatureId());
		postLayerFeature.setItem1(postLayerFeatureForm.getItem1());
		postLayerFeature.setItem2(postLayerFeatureForm.getItem2());
		postLayerFeature.setItem3(postLayerFeatureForm.getItem3());
		postLayerFeature.setItem4(postLayerFeatureForm.getItem4());
		postLayerFeature.setItem5(postLayerFeatureForm.getItem5());
		postLayerFeature.setItem6(postLayerFeatureForm.getItem6());
		postLayerFeature.setItem7(postLayerFeatureForm.getItem7());
		postLayerFeature.setItem8(postLayerFeatureForm.getItem8());
		postLayerFeature.setItem9(postLayerFeatureForm.getItem9());
		postLayerFeature.setItem10(postLayerFeatureForm.getItem10());
		postLayerFeatureRepository.save(postLayerFeature);
		LOGGER.info("投稿レイヤ情報を更新 featureId: " + postLayerFeature.getFeatureId());
	}
	
	/**
	 * 投稿レイヤの削除
	 * 
	 */
	@Transactional
	public Integer delete(DeletePostLayerForm postLayerForm) {
		 //投稿レイヤの削除
		int deleteCount = postLayerFeatureRepository.deleteByFeatureId(postLayerForm.getFeatureId());
		LOGGER.info("投稿レイヤの削除 featureId: " + postLayerForm.getFeatureId());
		Integer newParentId = postLayerForm.getParentFeatureId();
		// 親の投稿レイヤ情報である場合、子の投稿レイヤ情報のparentFeatureIdを更新する
		if (deleteCount > 0
				&& postLayerForm.getParentFeatureId().intValue() == postLayerForm.getFeatureId().intValue()) {
			List<PostLayerFeature> postLayerList = postLayerFeatureRepository.findByNotCurrentFeatureIdParentFeatureId(
					postLayerForm.getFeatureId(), postLayerForm.getParentFeatureId());
			if (postLayerList != null && postLayerList.size() > 0) {
				if (postLayerList.get(0) != null) {
					newParentId = postLayerList.get(0).getFeatureId();
					for (PostLayerFeature postLayer : postLayerList) {
						postLayer.setParentFeatureId(newParentId);
						postLayerFeatureRepository.save(postLayer);
						LOGGER.info("parentActivityIDの更新 activityId: " + postLayer.getFeatureId()
								+ " , parentActivityId: " + postLayer.getParentFeatureId());
					}
				}
			}
		}
		// 削除後の投稿レイヤIDを返す
		return newParentId;
	}
	
	/**
	 * 投稿情報の公開設定更新
	 * 
	 * @param activityFormList 活動フォームリスト
	 * @return result true:更新成功 false:更新失敗
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updatePublish(List<PostLayerFeatureForm> postLayerFeatureFormList) {
		boolean result = true;
		for(PostLayerFeatureForm postLayerFeatureForm : postLayerFeatureFormList) {
			Optional<PostLayerFeature> postLayerFeatureOpt = postLayerFeatureRepository.findById(postLayerFeatureForm.getFeatureId());
			if(postLayerFeatureOpt.isPresent()) {
				PostLayerFeature postLayerFeature = postLayerFeatureOpt.get();
				postLayerFeature.setPublishFlag(postLayerFeatureForm.getPublishFlag());
				postLayerFeatureRepository.save(postLayerFeature);
			}else {
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param postLayerAttributeList 投稿レイヤ属性Entityリスト
	 * @return postLayerAttributeFormList　投稿レイヤ属性フォームリスト
	 */
	public List<PostLayerAttributeForm> entityToFormForPostLayerAttribute(List<PostLayerAttribute> postLayerAttributeList) {
		List<PostLayerAttributeForm> postLayerAttributeFormList = new ArrayList<PostLayerAttributeForm>();
		for(PostLayerAttribute postLayerAttribute:postLayerAttributeList) {
			PostLayerAttributeForm postLayerAttributeForm = new PostLayerAttributeForm();
			postLayerAttributeForm.setItemId(postLayerAttribute.getId().getItemId());
			postLayerAttributeForm.setLayerId(postLayerAttribute.getId().getLayerId());
			postLayerAttributeForm.setItemName(postLayerAttribute.getItemName());
			postLayerAttributeForm.setItemType(postLayerAttribute.getItemType());
			postLayerAttributeForm.setDispOrder(postLayerAttribute.getDispOrder());
			postLayerAttributeFormList.add(postLayerAttributeForm);
		}
		return postLayerAttributeFormList;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param postLayerFeatureList 投稿レイヤEntityリスト
	 * @return postLayerFeatureFormList　投稿レイヤフォームリスト
	 */
	public List<PostLayerFeatureForm> entityToFormForPostLayerFeature(List<PostLayerFeatureExtra> postLayerFeatureList) {
		List<PostLayerFeatureForm> postLayerFeatureFormList = new ArrayList<PostLayerFeatureForm>();
		for(PostLayerFeatureExtra postLayerFeature:postLayerFeatureList) {
			PostLayerFeatureForm postLayerFeatureForm = new PostLayerFeatureForm();
			int featureId = postLayerFeature.getFeatureId().intValue();
			postLayerFeatureForm.setFeatureId(postLayerFeature.getFeatureId());
			postLayerFeatureForm.setLayerId(postLayerFeature.getLayerId());
			postLayerFeatureForm.setLongitude(postLayerFeature.getLongitude());
			postLayerFeatureForm.setLatitude(postLayerFeature.getLatitude());
			postLayerFeatureForm.setPublishFlag(postLayerFeature.getPublishFlag());
			postLayerFeatureForm.setPostUserId(postLayerFeature.getPostUserId());
			postLayerFeatureForm.setParentFeatureId(postLayerFeature.getParentFeatureId());
			postLayerFeatureForm.setPostDatetime(postLayerFeature.getPostDatetime());
			postLayerFeatureForm.setItem1(postLayerFeature.getItem1());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem1()) != null) postLayerFeatureForm.setItem1(checkAttachmentFile(featureId, postLayerFeature.getItem1()));
			postLayerFeatureForm.setItem2(postLayerFeature.getItem2());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem2()) != null) postLayerFeatureForm.setItem2(checkAttachmentFile(featureId, postLayerFeature.getItem2()));
			postLayerFeatureForm.setItem3(postLayerFeature.getItem3());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem3()) != null) postLayerFeatureForm.setItem3(checkAttachmentFile(featureId, postLayerFeature.getItem3()));
			postLayerFeatureForm.setItem4(postLayerFeature.getItem4());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem4()) != null) postLayerFeatureForm.setItem4(checkAttachmentFile(featureId, postLayerFeature.getItem4()));
			postLayerFeatureForm.setItem5(postLayerFeature.getItem5());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem5()) != null) postLayerFeatureForm.setItem5(checkAttachmentFile(featureId, postLayerFeature.getItem5()));
			postLayerFeatureForm.setItem6(postLayerFeature.getItem6());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem6()) != null) postLayerFeatureForm.setItem6(checkAttachmentFile(featureId, postLayerFeature.getItem6()));
			postLayerFeatureForm.setItem7(postLayerFeature.getItem7());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem7()) != null) postLayerFeatureForm.setItem7(checkAttachmentFile(featureId, postLayerFeature.getItem7()));
			postLayerFeatureForm.setItem8(postLayerFeature.getItem8());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem8()) != null) postLayerFeatureForm.setItem8(checkAttachmentFile(featureId, postLayerFeature.getItem8()));
			postLayerFeatureForm.setItem9(postLayerFeature.getItem9());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem9()) != null) postLayerFeatureForm.setItem9(checkAttachmentFile(featureId, postLayerFeature.getItem9()));
			postLayerFeatureForm.setItem10(postLayerFeature.getItem10());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem10()) != null) postLayerFeatureForm.setItem10(checkAttachmentFile(featureId, postLayerFeature.getItem10()));
			postLayerFeatureFormList.add(postLayerFeatureForm);
		}
		return postLayerFeatureFormList;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param postLayerFeatureList 投稿レイヤEntityリスト
	 * @return postLayerFeatureFormList　投稿レイヤフォームリスト
	 */
	public List<PostLayerFeatureForm> entityToFormForPostLayerFeature2(List<PostLayerFeatureExtra> postLayerFeatureList) {
		List<PostLayerFeatureForm> postLayerFeatureFormList = new ArrayList<PostLayerFeatureForm>();
		for(PostLayerFeatureExtra postLayerFeature:postLayerFeatureList) {
			PostLayerFeatureForm postLayerFeatureForm = new PostLayerFeatureForm();
			int featureId = postLayerFeature.getFeatureId().intValue();
			postLayerFeatureForm.setFeatureId(postLayerFeature.getFeatureId());
			postLayerFeatureForm.setLayerId(postLayerFeature.getLayerId());
			postLayerFeatureForm.setLongitude(postLayerFeature.getLongitude());
			postLayerFeatureForm.setLatitude(postLayerFeature.getLatitude());
			postLayerFeatureForm.setPublishFlag(postLayerFeature.getPublishFlag());
			postLayerFeatureForm.setPostUserId(postLayerFeature.getPostUserId());
			postLayerFeatureForm.setParentFeatureId(postLayerFeature.getParentFeatureId());
			postLayerFeatureForm.setPostDatetime(postLayerFeature.getPostDatetime());
			postLayerFeatureForm.setItem1(postLayerFeature.getItem1());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem1()) != null) postLayerFeatureForm.setItem1(postLayerFeature.getItem1());
			postLayerFeatureForm.setItem2(postLayerFeature.getItem2());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem2()) != null) postLayerFeatureForm.setItem2(postLayerFeature.getItem2());
			postLayerFeatureForm.setItem3(postLayerFeature.getItem3());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem3()) != null) postLayerFeatureForm.setItem3(postLayerFeature.getItem3());
			postLayerFeatureForm.setItem4(postLayerFeature.getItem4());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem4()) != null) postLayerFeatureForm.setItem4(postLayerFeature.getItem4());
			postLayerFeatureForm.setItem5(postLayerFeature.getItem5());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem5()) != null) postLayerFeatureForm.setItem5(postLayerFeature.getItem5());
			postLayerFeatureForm.setItem6(postLayerFeature.getItem6());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem6()) != null) postLayerFeatureForm.setItem6(postLayerFeature.getItem6());
			postLayerFeatureForm.setItem7(postLayerFeature.getItem7());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem7()) != null) postLayerFeatureForm.setItem7(postLayerFeature.getItem7());
			postLayerFeatureForm.setItem8(postLayerFeature.getItem8());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem8()) != null) postLayerFeatureForm.setItem8(postLayerFeature.getItem8());
			postLayerFeatureForm.setItem9(postLayerFeature.getItem9());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem9()) != null) postLayerFeatureForm.setItem9(postLayerFeature.getItem9());
			postLayerFeatureForm.setItem10(postLayerFeature.getItem10());
			if(checkAttachmentFile(featureId, postLayerFeature.getItem10()) != null) postLayerFeatureForm.setItem10(postLayerFeature.getItem10());
			postLayerFeatureFormList.add(postLayerFeatureForm);
		}
		return postLayerFeatureFormList;
	}
}
