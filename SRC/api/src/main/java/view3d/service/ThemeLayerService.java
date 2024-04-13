package view3d.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.PostLayerFeature;
import view3d.entity.Theme;
import view3d.entity.ThemeGraphList;
import view3d.entity.ThemeGraphListPK;
import view3d.entity.ThemeLayer;
import view3d.entity.ThemeLayerPK;
import view3d.form.LayerForm;
import view3d.form.PostLayerFeatureForm;
import view3d.form.ThemeForm;
import view3d.form.ThemeGraphListForm;
import view3d.form.ThemeLayerForm;
import view3d.repository.ThemeLayerRepository;
import view3d.util.SQLUtil;

@Service
public class ThemeLayerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThemeLayerService.class);
	
	@Autowired
	ThemeLayerRepository themeLayerRepository;
	
	@Autowired
	LayerService layerService;
	
	@Autowired
	ThemeService themeService;
	
	/**
	 * テーマレイヤ情報の取得（layer_settingsの切替項目値をテーマのデフォルトプレースホルダ値で置き換え）
	 * 
	 * @param themeId テーマID
	 * @return themeLayerFormList テーマレイヤFormリスト
	 */
	public List<ThemeLayerForm> findByThemeId(Integer themeId) {
		LOGGER.info("テーマレイヤ情報の取得開始 themeId:" + themeId);
		List<ThemeLayerForm> themeLayerFormList = null;
		List<ThemeLayer> themeLayerList = themeLayerRepository.findByThemeId(themeId);
		if(themeLayerList != null && themeLayerList.size() > 0) {
			themeLayerFormList = entityToForm(themeLayerList);
		}
		LOGGER.info("テーマレイヤ情報の取得終了 themeId:" + themeId);
		return themeLayerFormList;
	}
	
	/**
	 * テーマレイヤ情報の取得（layer_settingsの切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param themeId テーマID
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return themeLayerFormList テーマレイヤFormリスト
	 */
	public List<ThemeLayerForm> findByThemeId(Integer themeId,Map<String, String> switchItemMap) {
		LOGGER.info("テーマレイヤ情報の取得開始 themeId:" + themeId);
		List<ThemeLayerForm> themeLayerFormList = null;
		List<ThemeLayer> themeLayerList = themeLayerRepository.findByThemeId(themeId);
		if(themeLayerList != null && themeLayerList.size() > 0) {
			themeLayerFormList = entityToForm(themeLayerList,switchItemMap);
		}
		LOGGER.info("テーマレイヤ情報の取得終了 themeId:" + themeId);
		return themeLayerFormList;
	}
	
	/**
	 * テーマレイヤ情報の更新
	 * 
	 * @param List<ThemeLayerForm> 投稿レイヤフィーチャフォーム
	 * @return 
	 */
	@Transactional
	public List<ThemeLayerForm> update(List<ThemeLayerForm> themeLayerFormList, Integer themeId) throws Exception{
		LOGGER.info("テーマレイヤ情報の更新開始");
		boolean result = false;
		List<ThemeLayerForm> res = null;
		List<ThemeLayer> themeLayerList = themeLayerRepository.findByThemeId(themeId);
		if(themeLayerFormList != null && themeLayerFormList.size() > 0) {
			for(ThemeLayerForm themeLayerForm: themeLayerFormList) {
				if(themeLayerForm == null) {
					LOGGER.info("更新情報が存在しないので削除");
					
					break;
				}
				ThemeLayerPK themeLayerPK = new ThemeLayerPK();
				themeLayerPK.setThemeId(themeLayerForm.getThemeId());
				themeLayerPK.setLayerId(themeLayerForm.getLayerId());
				LOGGER.info("themeId:" + themeLayerPK.getThemeId());
				LOGGER.info("layerId:" + themeLayerPK.getLayerId());
				Optional<ThemeLayer> themeLayerOpt = themeLayerRepository.findById(themeLayerPK);
				if(themeLayerOpt.isPresent() && themeLayerOpt.get() != null) {
					ThemeLayer themeLayer = themeLayerOpt.get();
					themeLayer.setPostFlag(themeLayerForm.getPostFlag());
					themeLayer.setDispOrder(themeLayerForm.getDispOrder());
					//LOGGER.debug("" + themeLayer);
					ThemeLayer resultThemeLayer = themeLayerRepository.saveAndFlush(themeLayer);
					if(resultThemeLayer == null) {
						result = false;
					}else {
						result = true;
					}
					if(!result) {
						throw new Exception("processing error");
					}
				}else{
					LOGGER.info("テーマレイヤ情報が存在しないので追加");
					ThemeLayer themeLayer = new ThemeLayer();
					themeLayer.setId(themeLayerPK);
					themeLayer.setPostFlag(themeLayerForm.getPostFlag());
					themeLayer.setDispOrder(themeLayerForm.getDispOrder());
					ThemeLayer resultThemeLayer = themeLayerRepository.saveAndFlush(themeLayer);
					if(resultThemeLayer == null) {
						result = false;
					}else {
						result = true;
					}
					if(!result) {
						throw new Exception("processing error");
					}
				}
			}
		}
		LOGGER.info("テーマレイヤ情報の更新終了");
		List<ThemeLayer>resultrList = themeLayerRepository.findByThemeId(themeId);
		if(themeLayerList != null && themeLayerList.size() > 0) {
			res = entityToForm(themeLayerList);
		}
		return res;
	}
	
	/**
	 * テーマレイヤ情報の削除
	 * 
	 * @param themeId テーマID
	 * @param List<ThemeLayerForm> 
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean delete(List<ThemeLayerForm> themeLayerFormList, Integer themeId) throws Exception {
		boolean result = false;
		LOGGER.info("テーマレイヤ情報の削除開始 themeId:" + themeId);
		if(themeLayerFormList != null && themeLayerFormList.size() > 0) {
			for(ThemeLayerForm themeLayerForm: themeLayerFormList) {
				ThemeLayerPK themeLayerPK = new ThemeLayerPK();
				themeLayerPK.setThemeId(themeLayerForm.getThemeId());
				themeLayerPK.setLayerId(themeLayerForm.getLayerId());
				LOGGER.info("themeId:" + themeLayerPK.getThemeId());
				LOGGER.info("layerId:" + themeLayerPK.getLayerId());
				Optional<ThemeLayer> themeLayerOpt = themeLayerRepository.findById(themeLayerPK);
				if(themeLayerOpt.isPresent() && themeLayerOpt.get() != null) {
					themeLayerRepository.deleteById(themeLayerPK);
					result = true;
				}else{
					LOGGER.info("テーマレイヤ情報が存在しないのでスキップ");
					result = true;
				}
			}
		}
		LOGGER.info("テーマレイヤ情報の削除終了 themeId:" + themeId);
		return result;
	}
	
	/**
	 * entity→form詰め替え(List)（layer_settingsの切替項目値をテーマのデフォルトプレースホルダ値で置き換え）
	 * 
	 * @param List<ThemeLayer> テーマレイヤリストのEntityリスト
	 * @return List<ThemeLayerForm> テーマレイヤリストのFormリスト
	 */
	public List<ThemeLayerForm> entityToForm(List<ThemeLayer> themeLayerList) {
		List<ThemeLayerForm> themeLayerFormList = new ArrayList<ThemeLayerForm>();
		for(ThemeLayer themeLayer:themeLayerList) {
			ThemeLayerForm themeLayerForm = new ThemeLayerForm();
			themeLayerForm.setThemeId(themeLayer.getId().getThemeId());
			themeLayerForm.setLayerId(themeLayer.getId().getLayerId());
			themeLayerForm.setDispOrder(themeLayer.getDispOrder());
			themeLayerForm.setPostFlag(themeLayer.getPostFlag());
			LayerForm layerForm = layerService.findByLayerId(themeLayerForm.getLayerId());
			try {
				String layerSettings = layerForm.getLayerSettings();
				List<ThemeForm> themeFormList = themeService.findAll();
				for(ThemeForm themeForm:themeFormList) {
					//切替項目でレイヤ設定を置き換える( 形式：{placeholeder_name} )
					if(themeForm != null && "1".equals(themeForm.getSwitchFlag()) && layerSettings != null && themeForm.getSwitchPlaceholderName() != null && themeForm.getSwitchPlaceholderDefaultValue() != null) {
						String replaceText = themeForm.getSwitchPlaceholderDefaultValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						layerSettings = layerSettings.replaceAll("\\Q" + themeForm.getSwitchPlaceholderName() + "\\E", replaceText);
					}
				}
				layerForm.setLayerSettings(layerSettings);
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
			themeLayerForm.setLayerForm(layerForm);
			themeLayerFormList.add(themeLayerForm);
		}
		return themeLayerFormList;
	}
	
	/**
	 * entity→form詰め替え(List)（layer_settingsの切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param List<ThemeLayer> テーマレイヤリストのEntityリスト
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return List<ThemeLayerForm> テーマレイヤリストのFormリスト
	 */
	public List<ThemeLayerForm> entityToForm(List<ThemeLayer> themeLayerList,Map<String, String> switchItemMap) {
		List<ThemeLayerForm> themeLayerFormList = new ArrayList<ThemeLayerForm>();
		for(ThemeLayer themeLayer:themeLayerList) {
			ThemeLayerForm themeLayerForm = new ThemeLayerForm();
			themeLayerForm.setThemeId(themeLayer.getId().getThemeId());
			themeLayerForm.setLayerId(themeLayer.getId().getLayerId());
			themeLayerForm.setDispOrder(themeLayer.getDispOrder());
			themeLayerForm.setPostFlag(themeLayer.getPostFlag());
			LayerForm layerForm = layerService.findByLayerId(themeLayerForm.getLayerId());
			try {
				String layerSettings = layerForm.getLayerSettings();
				if(switchItemMap != null && layerSettings != null) {
					Iterator<String> iterator = switchItemMap.keySet().iterator();
					while(iterator.hasNext()) {
						String key = iterator.next();
						layerSettings = layerSettings.replaceAll("\\Q" + key + "\\E", switchItemMap.get(key));
					}
				}
				//デフォルトの切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
				List<ThemeForm> themeFormList = themeService.findAll();
				for(ThemeForm themeForm:themeFormList) {
					//切替項目でレイヤ設定を置き換える( 形式：{placeholeder_name} )
					if(themeForm != null && "1".equals(themeForm.getSwitchFlag()) && layerSettings != null && themeForm.getSwitchPlaceholderName() != null && themeForm.getSwitchPlaceholderDefaultValue() != null) {
						String replaceText = themeForm.getSwitchPlaceholderDefaultValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						layerSettings = layerSettings.replaceAll("\\Q" + themeForm.getSwitchPlaceholderName() + "\\E", replaceText);
					}
				}
				layerForm.setLayerSettings(layerSettings);
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
			themeLayerForm.setLayerForm(layerForm);
			themeLayerFormList.add(themeLayerForm);
		}
		return themeLayerFormList;
	}
	
}
