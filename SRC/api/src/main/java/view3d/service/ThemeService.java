package view3d.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.dao.ExecuteQueryDao;
import view3d.entity.GraphList;
import view3d.entity.Theme;
import view3d.entity.ThemeLayer;
import view3d.form.ThemeForm;
import view3d.repository.ThemeRepository;

@Service
public class ThemeService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThemeService.class);
	
	@Autowired
	ThemeRepository themeRepository;
	
	@Autowired
	ExecuteQueryDao executeQueryDao;
	
	/**
	 * テーマ情報の取得
	 * 
	 * @param themeId テーマID
	 * @return ThemeForm テーマForm
	 */
	public Theme findByThemeId(Integer themeId) {
		LOGGER.info("テーマ情報の取得開始 themeId:" + themeId);
		Theme theme = null;
		Optional<Theme> themeOpt = themeRepository.findById(themeId);
		if(themeOpt.isPresent()) {
			theme = themeOpt.get();
		}
		LOGGER.info("テーマ情報の取得終了 themeId:" + themeId);
		return theme;
	}
	
	/**
	 * テーマ情報の取得
	 * 
	 * @param themeId テーマID
	 * @return ThemeForm テーマForm
	 */
	public ThemeForm findByThemeIdForForm(Integer themeId) {
		LOGGER.info("テーマ情報の取得開始 themeId:" + themeId);
		ThemeForm themeForm = null;
		Optional<Theme> themeOpt = themeRepository.findById(themeId);
		if(themeOpt.isPresent()) {
			themeForm = entityToForm(themeOpt.get());
		}
		LOGGER.info("テーマ情報の取得終了 themeId:" + themeId);
		return themeForm;
	}
	
	/**
	 * デフォルトテーマ情報の取得
	 * 指定のthemeが非公開の場合自動で公開済みのテーマを返却する
	 * 
	 * @param themeId テーマID
	 * @return ThemeForm テーマForm
	 */
	public ThemeForm findDefaultThemeByThemeId(Integer themeId) {
		LOGGER.info("デフォルトテーマ情報の取得開始 themeId:" + themeId);
		ThemeForm themeForm = null;
		Optional<Theme> themeOpt = themeRepository.findById(themeId);
		if(themeOpt.isPresent()) {
			if("1".equals(themeOpt.get().getPublishFlag())) {
				themeForm = entityToForm(themeOpt.get());
			}else {
				themeOpt = themeRepository.findDefaultTheme();
				if(themeOpt.isPresent()) {
					themeForm = entityToForm(themeOpt.get());
					LOGGER.info("デフォルトテーマ情報を自動で変更 themeId:" + themeForm.getThemeId());
				}
			}
		}
		LOGGER.info("デフォルトテーマ情報の取得終了 themeId:" + themeId);
		return themeForm;
	}
	
	/**
	 * テーマ内切替項目の取得
	 * 
	 * @param themeId テーマiD
	 * @return resultList テーマ内切替項目
	 */
	public List<Map<String, Object>> getWithInThemeSwitchItem(Integer themeId) {
		LOGGER.info("テーマ内切替項目の取得開始 themeId:" + themeId);
		List<Map<String, Object>> resultList = null;
		Optional<Theme> themeOpt = themeRepository.findById(themeId);
		if(themeOpt.isPresent()) {
			try {
				if("1".equals(themeOpt.get().getSwitchFlag())) {
					resultList = executeQueryDao.executeQuery(themeOpt.get().getSwitchQuery());
				}
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		LOGGER.info("テーマ内切替項目の取得終了 themeId:" + themeId);
		return resultList;
	}
	
	/**
	 * 全ての公開済みテーマ情報の取得
	 * 
	 * @return List<ThemeForm> テーマFormリスト
	 */
	public List<ThemeForm> findAllLimited() {
		LOGGER.info("全てのテーマ情報の取得開始");
		List<ThemeForm> themeFormList = null;
		List<Theme> themeList = themeRepository.findAllByOrderBydispLimited();
		if(themeList != null && themeList.size() > 0) {
			themeFormList = entityToForm(themeList);
		}
		LOGGER.info("全てのテーマ情報の取得終了");
		return themeFormList;
	}
	
	/**
	 * 全てのテーマ情報の取得
	 * 
	 * @return List<ThemeForm> テーマFormリスト
	 */
	public List<ThemeForm> findAll() {
		LOGGER.info("全てのテーマ情報の取得開始");
		List<ThemeForm> themeFormList = null;
		List<Theme> themeList = themeRepository.findAllByOrderBydisp();
		if(themeList != null && themeList.size() > 0) {
			themeFormList = entityToForm(themeList);
		}
		LOGGER.info("全てのテーマ情報の取得終了");
		return themeFormList;
	}
	
	/**
	 * テーマ情報の更新
	 * 
	 * @param Theme     テーマエンティティ
	 * @param ThemeForm テーマフィーチャフォーム
	 */
	@Transactional
	public void update(Theme theme, ThemeForm themeForm) {
		theme.setPublishFlag(themeForm.getPublishFlag());
		theme.setPostFlag(themeForm.getPostFlag());
		theme.setThemeName(themeForm.getThemeName());
		theme.setThemeGroupName(themeForm.getThemeGroupName());
		theme.setDispOrder(themeForm.getDispOrder());
		themeRepository.save(theme);
		LOGGER.info("テーマ情報を更新 themeId: " + theme.getThemeId());
	}	
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param theme テーマEntity
	 * @return ThemeForm テーマForm
	 */
	public ThemeForm entityToForm(Theme theme) {
		ThemeForm themeForm = new ThemeForm();
		if(theme != null) {
			themeForm.setDispOrder(theme.getDispOrder());
			themeForm.setInformationText(theme.getInformationText());
			themeForm.setPostFlag(theme.getPostFlag());
			themeForm.setPublishFlag(theme.getPublishFlag());
			themeForm.setThemeGroupName(theme.getThemeGroupName());
			themeForm.setThemeId(theme.getThemeId());
			themeForm.setThemeName(theme.getThemeName());
			themeForm.setSwitchFlag(theme.getSwitchFlag());
			List<Map<String, Object>> resultList = null;
			if("1".equals(theme.getSwitchFlag())) {
				try {
					resultList = executeQueryDao.executeQuery(theme.getSwitchQuery());
				}catch(Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
			themeForm.setSwitchItemList(resultList);
			themeForm.setSwitchItemNameColumnName(theme.getSwitchItemNameColumnName());
			themeForm.setSwitchItemValueColumnName(theme.getSwitchItemValueColumnName());
			themeForm.setSwitchPlaceholderName(theme.getSwitchPlaceholderName());
			themeForm.setSwitchPlaceholderDefaultValue(theme.getSwitchPlaceholderDefaultValue());
		}
		return themeForm;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param themeList テーマEntityリスト
	 * @return List<ThemeForm> テーマFormリスト
	 */
	public List<ThemeForm> entityToForm(List<Theme> themeList) {
		List<ThemeForm> themeFormList = new ArrayList<ThemeForm>();
		for(Theme theme:themeList) {
			ThemeForm themeForm = new ThemeForm();
			themeForm.setDispOrder(theme.getDispOrder());
			themeForm.setInformationText(theme.getInformationText());
			themeForm.setPostFlag(theme.getPostFlag());
			themeForm.setPublishFlag(theme.getPublishFlag());
			themeForm.setThemeGroupName(theme.getThemeGroupName());
			themeForm.setThemeId(theme.getThemeId());
			themeForm.setThemeName(theme.getThemeName());
			themeForm.setSwitchFlag(theme.getSwitchFlag());
			List<Map<String, Object>> resultList = null;
			if("1".equals(theme.getSwitchFlag())) {
				try {
					resultList = executeQueryDao.executeQuery(theme.getSwitchQuery());
				}catch(Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
			themeForm.setSwitchItemList(resultList);
			themeForm.setSwitchItemNameColumnName(theme.getSwitchItemNameColumnName());
			themeForm.setSwitchItemValueColumnName(theme.getSwitchItemValueColumnName());
			themeForm.setSwitchPlaceholderName(theme.getSwitchPlaceholderName());
			themeForm.setSwitchPlaceholderDefaultValue(theme.getSwitchPlaceholderDefaultValue());
			themeFormList.add(themeForm);
		}
		return themeFormList;
	}

}
