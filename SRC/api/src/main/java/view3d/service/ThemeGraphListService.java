package view3d.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.ThemeGraphList;
import view3d.entity.ThemeGraphListPK;
import view3d.form.GraphListForm;
import view3d.form.ThemeGraphListForm;
import view3d.repository.ThemeGraphListRepository;

@Service
public class ThemeGraphListService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThemeGraphListService.class);
	
	@Autowired
	ThemeGraphListRepository themeGraphListRepository;
	
	@Autowired
	GraphListService graphListService;
	
	/**
	 * テーマグラフリスト情報を全件取得（グラフ・リストデータなし）
	 * 
	 * @return themeGraphListFormList テーマグラフリストFormリスト
	 */
	public List<ThemeGraphListForm> findAllNoDataList() {
		LOGGER.info("テーマグラフリスト情報の取得開始");
		List<ThemeGraphListForm> themeGraphListFormList = null;
		List<ThemeGraphList> themeGraphListList = themeGraphListRepository.findAll();
		if(themeGraphListList != null && themeGraphListList.size() > 0) {
			themeGraphListFormList = entityToFormNoDataList(themeGraphListList);
		}
		LOGGER.info("テーマグラフリスト情報の取得終了");
		return themeGraphListFormList;
	}
	
	/**
	 * テーマグラフリスト情報の取得（query_textの切替項目値をテーマのデフォルトプレースホルダ値で置き換え）
	 * 
	 * @param themeId テーマID
	 * @return themeGraphListFormList テーマグラフリストFormリスt
	 */
	public List<ThemeGraphListForm> findByThemeId(Integer themeId) {
		LOGGER.info("テーマグラフリスト情報の取得開始 themeId:" + themeId);
		List<ThemeGraphListForm> themeGraphListFormList = null;
		List<ThemeGraphList> themeGraphListList = themeGraphListRepository.findByThemeId(themeId);
		if(themeGraphListList != null && themeGraphListList.size() > 0) {
			themeGraphListFormList = entityToForm(themeGraphListList);
		}
		LOGGER.info("テーマグラフリスト情報の取得終了 themeId:" + themeId);
		return themeGraphListFormList;
	}
	
	/**
	 * テーマグラフリスト情報の取得（query_textの切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param themeId テーマID
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return themeGraphListFormList テーマグラフリストFormリスt
	 */
	public List<ThemeGraphListForm> findByThemeId(Integer themeId,Map<String, String> switchItemMap) {
		LOGGER.info("テーマグラフリスト情報の取得開始 themeId:" + themeId);
		List<ThemeGraphListForm> themeGraphListFormList = null;
		List<ThemeGraphList> themeGraphListList = themeGraphListRepository.findByThemeId(themeId);
		if(themeGraphListList != null && themeGraphListList.size() > 0) {
			themeGraphListFormList = entityToForm(themeGraphListList,switchItemMap);
		}
		LOGGER.info("テーマグラフリスト情報の取得終了 themeId:" + themeId);
		return themeGraphListFormList;
	}
	
	/**
	 * テーマグラフリスト情報の更新
	 * 
	 * @param 更新結果
	 * @return themeGraphListFormList テーマグラフリストFormリスト
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateThemeGraphListFormList(List<ThemeGraphListForm> themeGraphListFormList) throws Exception {
		LOGGER.info("テーマグラフリスト情報の更新開始");
		boolean result = false;
		if(themeGraphListFormList != null && themeGraphListFormList.size() > 0) {
			for(ThemeGraphListForm themeGraphListForm:themeGraphListFormList) {
				ThemeGraphListPK themeGraphListPK = new ThemeGraphListPK();
				themeGraphListPK.setGraphId(themeGraphListForm.getGraphId());
				themeGraphListPK.setThemeId(themeGraphListForm.getThemeId());
				LOGGER.info("graphId:" + themeGraphListPK.getGraphId());
				LOGGER.info("themeId:" + themeGraphListPK.getThemeId());
				Optional<ThemeGraphList> themeGraphListOpt = themeGraphListRepository.findById(themeGraphListPK);
				if(themeGraphListOpt.isPresent()) {
					ThemeGraphList themeGraphList = themeGraphListOpt.get();
					themeGraphList.setPanelHeight(themeGraphListForm.getPanelHeight());
					themeGraphList.setPanelWidth(themeGraphListForm.getPanelWidth());
					themeGraphList.setTopLeftX(themeGraphListForm.getTopLeftX());
					themeGraphList.setTopLeftY(themeGraphListForm.getTopLeftY());
					ThemeGraphList resultThemeGraphList = themeGraphListRepository.saveAndFlush(themeGraphList);
					if(resultThemeGraphList == null) {
						result = false;
					}else {
						result = true;
					}
					if(!result) {
						throw new Exception("processing error");
					}
				}else {
					throw new Exception("processing error");
				}
			}
		}
		LOGGER.info("テーマグラフリスト情報の更新終了");
		return result;
	}
	
	/**
	 * テーマグラフリスト情報の作成
	 * 
	 * @param 作成結果
	 * @return themeGraphListFormList テーマグラフリストFormリスト
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean registerThemeGraphListFormList(List<ThemeGraphListForm> themeGraphListFormList) throws Exception {
		LOGGER.info("テーマグラフリスト情報の作成開始");
		boolean result = false;
		if(themeGraphListFormList != null && themeGraphListFormList.size() > 0) {
			for(ThemeGraphListForm themeGraphListForm:themeGraphListFormList) {
				ThemeGraphListPK themeGraphListPK = new ThemeGraphListPK();
				themeGraphListPK.setGraphId(themeGraphListForm.getGraphId());
				themeGraphListPK.setThemeId(themeGraphListForm.getThemeId());
				LOGGER.info("graphId:" + themeGraphListPK.getGraphId());
				LOGGER.info("themeId:" + themeGraphListPK.getThemeId());
				Optional<ThemeGraphList> themeGraphListOpt = themeGraphListRepository.findById(themeGraphListPK);
				if(!themeGraphListOpt.isPresent()) {
					ThemeGraphList themeGraphList = new ThemeGraphList();
					themeGraphList.setId(themeGraphListPK);
					themeGraphList.setPanelHeight(themeGraphListForm.getPanelHeight());
					themeGraphList.setPanelWidth(themeGraphListForm.getPanelWidth());
					themeGraphList.setTopLeftX(themeGraphListForm.getTopLeftX());
					themeGraphList.setTopLeftY(themeGraphListForm.getTopLeftY());
					ThemeGraphList resultThemeGraphList = themeGraphListRepository.saveAndFlush(themeGraphList);
					if(resultThemeGraphList == null) {
						result = false;
					}else {
						result = true;
					}
					if(!result) {
						throw new Exception("processing error");
					}
				}else {
					throw new Exception("processing error");
				}
			}
		}
		LOGGER.info("テーマグラフリスト情報の作成終了");
		return result;
	}
	
	/**
	 * テーマグラフリスト情報の削除
	 * 
	 * @param 削除結果
	 * @return themeGraphListFormList テーマグラフリストFormリスト
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteThemeGraphListFormList(List<ThemeGraphListForm> themeGraphListFormList) throws Exception {
		LOGGER.info("テーマグラフリスト情報の削除開始");
		boolean result = false;
		if(themeGraphListFormList != null && themeGraphListFormList.size() > 0) {
			for(ThemeGraphListForm themeGraphListForm:themeGraphListFormList) {
				ThemeGraphListPK themeGraphListPK = new ThemeGraphListPK();
				themeGraphListPK.setGraphId(themeGraphListForm.getGraphId());
				themeGraphListPK.setThemeId(themeGraphListForm.getThemeId());
				LOGGER.info("graphId:" + themeGraphListPK.getGraphId());
				LOGGER.info("themeId:" + themeGraphListPK.getThemeId());
				Optional<ThemeGraphList> themeGraphListOpt = themeGraphListRepository.findById(themeGraphListPK);
				if(themeGraphListOpt.isPresent()) {
					int deleteCount = themeGraphListRepository.deleteByThemeIdAndGraphId(themeGraphListPK.getThemeId(),themeGraphListPK.getGraphId());
					if(deleteCount < 1) {
						result = false;
					}else {
						result = true;
					}
					if(!result) {
						throw new Exception("processing error");
					}
				}else {
					throw new Exception("processing error");
				}
			}
		}
		LOGGER.info("テーマグラフリスト情報の削除終了");
		return result;
	}
	
	/**
	 * entity→form詰め替え(List)（query_textの切替項目値をテーマのデフォルトプレースホルダ値で置き換え）
	 * 
	 * @param List<ThemeGraphList> テーマグラフリストのEntityリスト
	 * @return List<ThemeGraphListForm> テーマグラフリストのFormリスト
	 */
	public List<ThemeGraphListForm> entityToForm(List<ThemeGraphList> themeGraphListList) {
		List<ThemeGraphListForm> themeGraphListFormList = new ArrayList<ThemeGraphListForm>();
		for(ThemeGraphList themeGraphList:themeGraphListList) {
			ThemeGraphListForm themeGraphListForm = new ThemeGraphListForm();
			themeGraphListForm.setGraphId(themeGraphList.getId().getGraphId());
			themeGraphListForm.setThemeId(themeGraphList.getId().getThemeId());
			GraphListForm graphListForm = graphListService.findByGraphListId(themeGraphListForm.getGraphId(),themeGraphListForm.getThemeId());
			themeGraphListForm.setGraphListForm(graphListForm);
			themeGraphListForm.setPanelHeight(themeGraphList.getPanelHeight());
			themeGraphListForm.setPanelWidth(themeGraphList.getPanelWidth());
			themeGraphListForm.setTopLeftX(themeGraphList.getTopLeftX());
			themeGraphListForm.setTopLeftY(themeGraphList.getTopLeftY());
			themeGraphListFormList.add(themeGraphListForm);
		}
		return themeGraphListFormList;
	}
	
	/**
	 * entity→form詰め替え(List)　（query_textの切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param List<ThemeGraphList> テーマグラフリストのEntityリスト
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return List<ThemeGraphListForm> テーマグラフリストのFormリスト
	 */
	public List<ThemeGraphListForm> entityToForm(List<ThemeGraphList> themeGraphListList,Map<String, String> switchItemMap) {
		List<ThemeGraphListForm> themeGraphListFormList = new ArrayList<ThemeGraphListForm>();
		for(ThemeGraphList themeGraphList:themeGraphListList) {
			ThemeGraphListForm themeGraphListForm = new ThemeGraphListForm();
			themeGraphListForm.setGraphId(themeGraphList.getId().getGraphId());
			themeGraphListForm.setThemeId(themeGraphList.getId().getThemeId());
			GraphListForm graphListForm = graphListService.findByGraphListId(themeGraphListForm.getGraphId(),themeGraphListForm.getThemeId(),switchItemMap);
			themeGraphListForm.setGraphListForm(graphListForm);
			themeGraphListForm.setPanelHeight(themeGraphList.getPanelHeight());
			themeGraphListForm.setPanelWidth(themeGraphList.getPanelWidth());
			themeGraphListForm.setTopLeftX(themeGraphList.getTopLeftX());
			themeGraphListForm.setTopLeftY(themeGraphList.getTopLeftY());
			themeGraphListFormList.add(themeGraphListForm);
		}
		return themeGraphListFormList;
	}
	
	/**
	 * entity→form詰め替え(List)（グラフ・リストデータなし）
	 * 
	 * @param List<ThemeGraphList> テーマグラフリストのEntityリスト
	 * @return List<ThemeGraphListForm> テーマグラフリストのFormリスト
	 */
	public List<ThemeGraphListForm> entityToFormNoDataList(List<ThemeGraphList> themeGraphListList) {
		List<ThemeGraphListForm> themeGraphListFormList = new ArrayList<ThemeGraphListForm>();
		for(ThemeGraphList themeGraphList:themeGraphListList) {
			ThemeGraphListForm themeGraphListForm = new ThemeGraphListForm();
			themeGraphListForm.setGraphId(themeGraphList.getId().getGraphId());
			themeGraphListForm.setThemeId(themeGraphList.getId().getThemeId());
			GraphListForm graphListForm = graphListService.findByGraphListIdNoDataList(themeGraphListForm.getGraphId());
			themeGraphListForm.setGraphListForm(graphListForm);
			themeGraphListForm.setPanelHeight(themeGraphList.getPanelHeight());
			themeGraphListForm.setPanelWidth(themeGraphList.getPanelWidth());
			themeGraphListForm.setTopLeftX(themeGraphList.getTopLeftX());
			themeGraphListForm.setTopLeftY(themeGraphList.getTopLeftY());
			themeGraphListFormList.add(themeGraphListForm);
		}
		return themeGraphListFormList;
	}
	

}
