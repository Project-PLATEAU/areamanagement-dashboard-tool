package view3d.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.dao.ExecuteQueryDao;
import view3d.entity.GraphList;
import view3d.entity.GraphListTemplateSettingPK;
import view3d.entity.GraphListTemplateVal;
import view3d.entity.GraphListTemplateValPK;
import view3d.entity.GraphListType;
import view3d.entity.LayerGraphCooporation;
import view3d.entity.LayerSourceField;
import view3d.entity.PostLayerAttribute;
import view3d.form.GraphListForm;
import view3d.form.GraphListRegisterForm;
import view3d.form.GraphListTemplateValForm;
import view3d.form.LayerGraphCooporationForm;
import view3d.form.LayerSourceFieldForm;
import view3d.form.LayerSourceForm;
import view3d.form.PostLayerAttributeForm;
import view3d.form.ThemeForm;
import view3d.repository.GraphListRepository;
import view3d.repository.GraphListTemplateSettingRepository;
import view3d.repository.GraphListTemplateValRepository;
import view3d.repository.GraphListTypeRepository;
import view3d.repository.LayerGraphCooporationRepository;
import view3d.repository.LayerSourceFieldRepository;
import view3d.repository.PostLayerAttributeRepository;
import view3d.repository.ThemeGraphListRepository;
import view3d.util.SQLUtil;

@Service
public class GraphListService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphListService.class);

	@Autowired
	GraphListRepository graphListRepository;
	
	@Autowired
	GraphListTypeRepository graphListTypeRepository;
	
	@Autowired
	GraphListTemplateValRepository graphListTemplateValRepository;

	@Autowired
	LayerGraphCooporationRepository layerGraphCooporationRepository;
	
	@Autowired
	ThemeGraphListRepository themeGraphListRepository;
	
	@Autowired
	LayerSourceFieldRepository layerSourceFieldRepository;
	
	@Autowired
	ExecuteQueryDao executeQueryDao;
	
	@Autowired
	ThemeService themeService;
	
	@Autowired
	GraphListTemplateValService graphListTemplateValService;
	
	@Autowired
	LayerGraphCooporationService layerGraphCooporationService;
	
	@Autowired
	LayerSourceService layerSourceService;
	
	@Autowired
	GraphListTemplateSettingService graphListTemplateSettingService;
	
	@Autowired
	GraphListTemplateSettingRepository graphListTemplateSettingRepository;
	
	@Autowired
	PostLayerAttributeRepository postLayerAttributeRepository;
	
	@Autowired
	EntityManager entityManager;
	
	/**
	 * グラフ・リスト情報の全件取得(グラフ・リストデータなし)
	 * 
	 * @return graphListFormList グラフリストFormリスト
	 */
	public List<GraphListForm> findAllNoDataList() {
		LOGGER.info("グラフ・リスト情報の全件取得開始");
		List<GraphListForm> graphListFormList = null;
		List<GraphList> graphListList = graphListRepository.findAll();
		if(graphListList != null && graphListList.size() > 0) {
			graphListFormList = entityToFormNoDataList(graphListList);
		}
		LOGGER.info("グラフ・リスト情報の全件取得終了");
		return graphListFormList;
	}
	
	/**
	 * グラフ・リスト情報の取得
	 * 
	 * @param graphListId グラフリストID
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm findByGraphListId(Integer graphListId) {
		LOGGER.info("グラフ・リスト情報の取得開始 graphListId:" + graphListId);
		GraphListForm graphListForm = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			graphListForm = entityToForm(graphListOpt.get());
		}
		LOGGER.info("グラフ・リスト情報の取得終了 graphListId:" + graphListId);
		return graphListForm;
	}
	
	/**
	 * グラフ・リスト情報の取得(query_textあり)
	 * 
	 * @param graphListId グラフリストID
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm findByGraphListIdForAdmin(Integer graphListId) {
		LOGGER.info("グラフ・リスト情報の取得開始 graphListId:" + graphListId);
		GraphListForm graphListForm = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			graphListForm = entityToForm(graphListOpt.get());
			graphListForm.setQueryText(graphListOpt.get().getQueryText());
		}
		LOGGER.info("グラフ・リスト情報の取得終了 graphListId:" + graphListId);
		return graphListForm;
	}
	
	/**
	 * グラフ・リスト情報の取得(グラフ・リストデータなし)
	 * 
	 * @param graphListId グラフリストID
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm findByGraphListIdNoDataList(Integer graphListId) {
		LOGGER.info("グラフ・リスト情報の取得開始 graphListId:" + graphListId);
		GraphListForm graphListForm = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			graphListForm = entityToFormNoDataList(graphListOpt.get());
		}
		LOGGER.info("グラフ・リスト情報の取得終了 graphListId:" + graphListId);
		return graphListForm;
	}
	
	/**
	 * グラフ・リスト情報の取得（query_textの切替項目値をテーマのデフォルトプレースホルダ値で置き換え）
	 * 
	 * @param graphListId グラフリストID
	 * @param themeId テーマID
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm findByGraphListId(Integer graphListId,Integer themeId) {
		LOGGER.info("グラフ・リスト情報の取得開始 graphListId:" + graphListId);
		GraphListForm graphListForm = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			graphListForm = entityToForm(graphListOpt.get());
		}
		LOGGER.info("グラフ・リスト情報の取得終了 graphListId:" + graphListId);
		return graphListForm;
	}
	
	/**
	 * グラフ・リスト情報の取得（query_textの切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param graphListId グラフリストID
	 * @param themeId テーマID
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm findByGraphListId(Integer graphListId,Integer themeId,Map<String, String> switchItemMap) {
		LOGGER.info("グラフ・リスト情報の取得開始 graphListId:" + graphListId);
		GraphListForm graphListForm = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			graphListForm = entityToForm(graphListOpt.get(),switchItemMap);
		}
		LOGGER.info("グラフ・リスト情報の取得終了 graphListId:" + graphListId);
		return graphListForm;
	}
	
	/**
	 * グラフ・リストデータの取得
	 * 
	 * @param graphListId グラフリストID
	 * @return resultList グラフリストデータ
	 * @throws Exception 
	 */
	public List<Map<String, Object>> checkGraphListData(Integer graphListId) throws Exception {
		LOGGER.info("グラフ・リストデータの取得開始 graphListId:" + graphListId);
		List<Map<String, Object>> resultList = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			GraphList graphList = graphListOpt.get();
			List<GraphListTemplateValForm> graphListTemplateValFormList = graphListTemplateValService.findByGraphId(graphList.getGraphId());
			String queryText = graphList.getQueryText();
			List<ThemeForm> themeFormList = themeService.findAll();
			for(ThemeForm themeForm:themeFormList) {
				//切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
				if(themeForm != null && "1".equals(themeForm.getSwitchFlag()) && queryText != null && themeForm.getSwitchPlaceholderName() != null && themeForm.getSwitchPlaceholderDefaultValue() != null) {
					String replaceText = themeForm.getSwitchPlaceholderDefaultValue();
					if(replaceText == null) {
						replaceText = "";
					}
					replaceText = SQLUtil.sqlEscape(replaceText);
					queryText = queryText.replaceAll("\\Q" + themeForm.getSwitchPlaceholderName() + "\\E", replaceText);
				}
			}
			//プレースホルダのカラムを置き換える( 形式：$placeholeder_name$ )
			for(GraphListTemplateValForm graphListTemplateValForm:graphListTemplateValFormList) {
				if(graphListTemplateValForm!=null && graphListTemplateValForm.getItemValue() != null && graphListTemplateValForm.getGraphListTemplateSettingForm() != null && "1".equals(graphListTemplateValForm.getGraphListTemplateSettingForm().getPlaceholderFlag())) {
					String replaceText = graphListTemplateValForm.getItemValue();
					if(replaceText == null) {
						replaceText = "";
					}
					replaceText = SQLUtil.sqlEscape(replaceText);
					queryText = queryText.replaceAll("\\Q" + graphListTemplateValForm.getGraphListTemplateSettingForm().getAttributeName() + "\\E", replaceText);
					//プレースホルダのカラム置き換えでFROM以降のas句は取り除く
					//簡易のSQLを動的queryの対象とする
					queryText = queryText.replaceAll("[\r\n]", "");
					if(replaceText.toLowerCase().contains(" as ")) {
						String replaceSubStringText = replaceText.substring(0,replaceText.toLowerCase().indexOf(" as "));
						String beforeQueryText = queryText.substring(0,queryText.toLowerCase().indexOf(" from "));
						String afterQueryText = queryText.substring(queryText.toLowerCase().indexOf(" from ")+1);
						afterQueryText = afterQueryText.replaceAll("\\Q" + replaceText + "\\E", SQLUtil.sqlEscape(replaceSubStringText));
						queryText = beforeQueryText + " " + afterQueryText;
					}
				}
			}
			resultList = executeQueryDao.executeQuery(queryText);
		}else {
			throw new Exception("processing error");
		}
		LOGGER.info("グラフ・リストデータの取得終了 graphListId:" + graphListId);
		return resultList;
	}
	
	/**
	 * グラフ・リストデータの取得（切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param graphListId グラフリストID
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return resultList グラフリストデータ
	 */
	public List<Map<String, Object>> getGraphListData(Integer graphListId,Map<String, String> switchItemMap) {
		LOGGER.info("グラフ・リストデータの取得開始 graphListId:" + graphListId);
		List<Map<String, Object>> resultList = null;
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListId);
		if(graphListOpt.isPresent()) {
			try {
				String queryText = graphListOpt.get().getQueryText();
				//切替項目でqueryテキストを置き換える
				if(switchItemMap != null) {
					Iterator<String> iterator = switchItemMap.keySet().iterator();
					while(iterator.hasNext()) {
						String key = iterator.next();
						String replaceText = switchItemMap.get(key);
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						queryText = queryText.replaceAll("\\Q" + key + "\\E", replaceText);
					}
				}
				resultList = executeQueryDao.executeQuery(queryText);
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		LOGGER.info("グラフ・リストデータの取得終了 graphListId:" + graphListId);
		return resultList;
	}
	
	/**
	 * グラフ・リスト情報の作成及び更新
	 * 
	 * @param graphListRegisterForm
	 * @return graphId　作成及び更新が成功した場合グラフID それ以外はnull
	 */
	@Transactional(rollbackFor = Exception.class)
	public Integer register(GraphListRegisterForm graphListRegisterForm) throws Exception {
		LOGGER.info("グラフ・リスト情報の作成開始 graphTypeId:" + graphListRegisterForm.getGraphTypeId());
		boolean editFlag = false;
		Integer editGraphTypeId = 0;
		Integer result = null;
		GraphList graphList = new GraphList();
		if(graphListRegisterForm.getGraphId() != null && graphListRegisterForm.getGraphId().intValue() > -1) {
			Optional<GraphList> graphListOpt = graphListRepository.findById(graphListRegisterForm.getGraphId());
			if(graphListOpt.isPresent()) {
				graphList = graphListOpt.get();
				editGraphTypeId = graphList.getGraphTypeId();
				editFlag = true;
			}
		}
		graphList.setEditFlag("1");
		graphList.setPlaceholderFlag("1");
		graphList.setGraphName(graphListRegisterForm.getGraphName());
		graphList.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
		graphList.setSourceId(graphListRegisterForm.getSourceId());
		LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphListRegisterForm.getSourceId());
		Optional<GraphListType> graphListTypeOpt = graphListTypeRepository.findById(graphListRegisterForm.getGraphTypeId());
		String LonLatPlaceHoderNameReplaceText = "";
		
		if(graphListTypeOpt.isPresent() && layerSourceForm != null && layerSourceForm.getTableName() != null) {
			
			String defaultQueryText = graphListTypeOpt.get().getDefaultQueryText();
			
			if(defaultQueryText != null && defaultQueryText != "") {
				GraphList newGraphList = null;
				if(!"1".equals(graphListRegisterForm.getEditRestrictionFlag())){
					//ソートモードを置き換え
					String sortMode = ""; 
					if(graphListRegisterForm.getSortModeMap() != null) {
						Map<String,String> sortModeMap = graphListRegisterForm.getSortModeMap();
						Iterator<String> iterator = sortModeMap.keySet().iterator();
						while(iterator.hasNext()) {
							String columnName = iterator.next();
							LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,columnName);
							if(layerSourceFieldForm != null) {
								String order = sortModeMap.get(columnName);
								if(order == null || "".equals(order)) {
									order = "ASC";
								}
								if(!"item_1".equals(columnName) && !"item_2".equals(columnName) && !"item_3".equals(columnName)
								  && !"item_4".equals(columnName) && !"item_5".equals(columnName) && !"item_6".equals(columnName)
								  && !"item_7".equals(columnName) && !"item_8".equals(columnName) && !"item_9".equals(columnName)
								  && !"item_10".equals(columnName)) {
									if(layerSourceFieldForm.getAlias() != null) {
										columnName = layerSourceFieldForm.getAlias();
										columnName = SQLUtil.aliasNameEscape(columnName);
										
									}
									sortMode = sortMode + " \""+columnName+"\" " + " "+order+" ,";
								}else {
									Integer itemId = Integer.parseInt(columnName.replace("item_", ""));
									PostLayerAttribute postLayerAttribute = postLayerAttributeRepository.findByLayerIdAndItemId(layerSourceForm.getLayerId(), itemId);
									Integer itemType = postLayerAttribute.getItemType();
									if(itemType.intValue() == 3 && (!"1".equals(graphListRegisterForm.getGroupByFlag()) || graphListRegisterForm.getGraphTypeId().intValue() == 5)) {
										sortMode = sortMode + " CASE WHEN isnumericex(cast(\"" + columnName + "\" as text)) THEN cast(\""+columnName+"\" as numeric) ELSE 0 END "+order+" ,";
									}else {
										if(layerSourceFieldForm.getAlias() != null) {
											columnName = layerSourceFieldForm.getAlias();
											columnName = SQLUtil.aliasNameEscape(columnName);
											
										}
										sortMode = sortMode + " \""+columnName+"\" " + " "+order+" ,";
									}
								}
							}
						}
					}
					if(sortMode.length() > 0) {
						sortMode = sortMode.substring(0, sortMode.length()-1);
					} else {
						defaultQueryText = defaultQueryText.replaceAll("\\QORDER BY\\E", "");
					}
					defaultQueryText = defaultQueryText.replaceAll("\\Q{{SortMode}}\\E", SQLUtil.sqlEscape(sortMode));
					
					//リミット数を置き換え（TODO:デフォルト数は外だし予定）
					Integer limitSize = graphListRegisterForm.getLimitSize();
					if(limitSize == null) {
						limitSize = 15;
					}
					defaultQueryText = defaultQueryText.replaceAll("\\Q{{LimitSize}}\\E", SQLUtil.sqlEscape(limitSize + ""));
					
					//経度・緯度のカラムを置き換え　geometry
					String geomColumnName = "";
					List<Map<String, Object>> resultListForGeom = executeQueryDao.executeQuery("select table_name,column_name from information_schema.columns where column_name = 'geom' OR column_name = 'geometry';");
					//集約関数有の場合geomは対象外にする
					if(!"1".equals(graphListRegisterForm.getGroupByFlag())) {
						if(resultListForGeom != null) {
							for(Map<String, Object> resultMap:resultListForGeom) {
								String tableName = (String) resultMap.get("table_name");
								if(tableName != null && layerSourceForm.getTableName() !=null && layerSourceForm.getTableName().equals(tableName)) {
									geomColumnName = (String) resultMap.get("column_name");
									LonLatPlaceHoderNameReplaceText = LonLatPlaceHoderNameReplaceText + " ((ST_XMax(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326))+ST_XMin(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326)))/2) AS longitude ";
									LonLatPlaceHoderNameReplaceText = LonLatPlaceHoderNameReplaceText + " , ((ST_YMax(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326))+ST_YMin(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326)))/2) AS latitude ";
								}
							}
						}
					}
					if(!"".equals(LonLatPlaceHoderNameReplaceText)) {
						defaultQueryText = defaultQueryText.replaceAll("\\Q{{LonLatPlaceHoderName}}\\E", SQLUtil.sqlEscape(LonLatPlaceHoderNameReplaceText));
					}else {
						defaultQueryText = defaultQueryText.replaceAll("\\Q,{{LonLatPlaceHoderName}}\\E", "");
					}
					
					//WHERE条件(公開フラグがセットされている場合は条件付与)
					String conditionsForPublishFlag = "";
					List<Map<String, Object>> resultListForPublishFlag = executeQueryDao.executeQuery("select table_name,column_name from information_schema.columns where column_name = 'publish_flag';");
					if(resultListForPublishFlag != null) {
						for(Map<String, Object> resultMap:resultListForPublishFlag) {
							String tableName = (String) resultMap.get("table_name");
							if(tableName != null && layerSourceForm.getTableName() !=null && layerSourceForm.getTableName().equals(tableName)) {
								conditionsForPublishFlag = " \"publish_flag\" = '1' ";
							}
						}
					}
					if(graphListRegisterForm.getGraphYColumn() != null && !"".equals(graphListRegisterForm.getGraphYColumn())) {
						if("".equals(conditionsForPublishFlag)) {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{YColumnPlaceHoderName}}\\E", "\"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphYColumn()) + "\"" );
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{YColumnPlaceHoderName}}\\E", conditionsForPublishFlag + " AND "+"\"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphYColumn()) + "\"" );
						}
					}else {
						if("".equals(conditionsForPublishFlag)) {
							defaultQueryText = defaultQueryText.replaceAll("\\QWHERE {{YColumnPlaceHoderName}} IS NOT NULL\\E", "");
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{YColumnPlaceHoderName}} IS NOT NULL\\E", conditionsForPublishFlag);
						}
					}
					//リストの場合の考慮
					if(graphListRegisterForm.getGraphTypeId().intValue() == 5) {
						if("".equals(conditionsForPublishFlag)) {
							defaultQueryText = defaultQueryText.replaceAll("\\QWHERE {{ConditionsForPublishFlag}}\\E", "");
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{ConditionsForPublishFlag}}\\E", conditionsForPublishFlag);
						}
					}
					
					//GROUP BY 条件
					if("1".equals(graphListRegisterForm.getGroupByFlag()) && graphListRegisterForm.getAggregationType() != null && graphListRegisterForm.getAggregationType().intValue() > 0 &&
							(graphListRegisterForm.getGraphTypeId().intValue() == 2 
							|| graphListRegisterForm.getGraphTypeId().intValue() == 3 
								|| graphListRegisterForm.getGraphTypeId().intValue() == 4)) {
						if(!"".equals(geomColumnName)) {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{GroupBy}}\\E", " GROUP BY \"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphXColumn()) + "\",\""+geomColumnName+"\" " );
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{GroupBy}}\\E", " GROUP BY \"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphXColumn()) + "\" " );
						}
						String itemValue = graphListRegisterForm.getGraphYColumn();
						LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
						if(layerSourceFieldForm != null) {
							String alias = layerSourceFieldForm.getAlias();
							alias = SQLUtil.aliasNameEscape(alias);
							switch(graphListRegisterForm.getAggregationType()) {
								case 1:
									if(alias != null) {
										itemValue = " SUM(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " SUM(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 2:
									if(alias != null) {
										itemValue = " AVG(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " AVG(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 3:
									if(alias != null) {
										itemValue = " MIN(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " MIN(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 4:
									if(alias != null) {
										itemValue = " MAX(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " MAX(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 5:
									if(alias != null) {
										itemValue = " COUNT(\"" + SQLUtil.sqlEscape(itemValue) + "\") as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " COUNT(\"" + SQLUtil.sqlEscape(itemValue) + "\") as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								default:
									break;
							}
						defaultQueryText = defaultQueryText.replaceAll("\\Q$YColumnPlaceHoderName$\\E", itemValue);
						}
					}else {
						defaultQueryText = defaultQueryText.replaceAll("\\Q{{GroupBy}}\\E", "" );
					}
					
					//グラフリストの作成
					graphList.setQueryText(defaultQueryText);
					newGraphList = graphListRepository.saveAndFlush(graphList);
					entityManager.refresh(newGraphList);
					
					//新規グラフリストの作成に失敗
					if(newGraphList == null || newGraphList.getGraphId() == null) {
						throw new Exception("processing error");
					}
				} else {
					if(graphList.getGraphId() != null) {
						newGraphList = graphList;
					}
				}
				
				//テンプレート項目値の設定
				//円グラフ or 棒グラフ or 線グラフの場合
				if(newGraphList != null 
					&& (graphListRegisterForm.getGraphTypeId().intValue() == 2 
						|| graphListRegisterForm.getGraphTypeId().intValue() == 3 
							|| graphListRegisterForm.getGraphTypeId().intValue() == 4)) {
					
					//競合する設定があれば削除
					if(!"1".equals(graphListRegisterForm.getEditRestrictionFlag()) && (!editFlag && newGraphList.getGraphTypeId() != null && editGraphTypeId.intValue() != newGraphList.getGraphTypeId().intValue())){
						try {
							graphListTemplateValRepository.deleteByGraphId(newGraphList.getGraphId());
						}catch(Exception e) {
							LOGGER.error(e.getMessage());
						}
					}
					
					//$YColumnPlaceHoderName$,$XColumnPlaceHoderName$,$TablePlaceHoderName$の生成
					GraphListTemplateVal graphListTemplateVal = new GraphListTemplateVal();
					GraphListTemplateValPK graphListTemplateValPK = new GraphListTemplateValPK();
					graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
					graphListTemplateValPK.setItemId(100);
					graphListTemplateVal.setId(graphListTemplateValPK);
					String itemValue = graphListRegisterForm.getGraphYColumn();
					String yColumnName = itemValue;
					LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
					if(layerSourceFieldForm != null) {
						String alias = layerSourceFieldForm.getAlias();
						alias = SQLUtil.aliasNameEscape(alias);
						if(alias != null) {
							itemValue = " \"" + itemValue + "\" as \"" + alias + "\" ";
							yColumnName = alias;
						}
						graphListTemplateVal.setItemValue(itemValue);
						graphListTemplateValRepository.save(graphListTemplateVal);
						graphListTemplateVal = new GraphListTemplateVal();
						graphListTemplateValPK = new GraphListTemplateValPK();
						graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
						graphListTemplateValPK.setItemId(1);
						graphListTemplateVal.setId(graphListTemplateValPK);
						graphListTemplateVal.setItemValue(yColumnName);
						graphListTemplateValRepository.save(graphListTemplateVal);
					}
					graphListTemplateVal = new GraphListTemplateVal();
					graphListTemplateValPK = new GraphListTemplateValPK();
					graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
					graphListTemplateValPK.setItemId(101);
					graphListTemplateVal.setId(graphListTemplateValPK);
					itemValue = graphListRegisterForm.getGraphXColumn();
					String xColumnName = itemValue;
					layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
					if(layerSourceFieldForm != null) {
						String alias = layerSourceFieldForm.getAlias();
						alias = SQLUtil.aliasNameEscape(alias);
						if(alias != null) {
							itemValue = " \"" + itemValue + "\" as \"" + alias + "\" ";
							xColumnName = alias;
						}
						graphListTemplateVal.setItemValue(itemValue);
						graphListTemplateValRepository.save(graphListTemplateVal);
						graphListTemplateVal = new GraphListTemplateVal();
						graphListTemplateValPK = new GraphListTemplateValPK();
						graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
						graphListTemplateValPK.setItemId(2);
						graphListTemplateVal.setId(graphListTemplateValPK);
						graphListTemplateVal.setItemValue(xColumnName);
						graphListTemplateValRepository.save(graphListTemplateVal);
					}
					
					if(graphListRegisterForm.getGraphTypeId().intValue() == 3 && graphListRegisterForm.getGraphDirection() != null){
						graphListTemplateVal = new GraphListTemplateVal();
						graphListTemplateValPK = new GraphListTemplateValPK();
						graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
						graphListTemplateValPK.setItemId(11);
						graphListTemplateVal.setId(graphListTemplateValPK);
						graphListTemplateVal.setItemValue(graphListRegisterForm.getGraphDirection());
						graphListTemplateValRepository.save(graphListTemplateVal);
					}
					
					graphListTemplateVal = new GraphListTemplateVal();
					graphListTemplateValPK = new GraphListTemplateValPK();
					graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
					graphListTemplateValPK.setItemId(102);
					graphListTemplateVal.setId(graphListTemplateValPK);
					if(layerSourceForm.getTableName() != null) {
						graphListTemplateVal.setItemValue(layerSourceForm.getTableName());
						graphListTemplateValRepository.save(graphListTemplateVal);
					}
					
					result = newGraphList.getGraphId();
					
				//リストの場合
				}else if(newGraphList != null 
							&& graphListRegisterForm.getGraphTypeId().intValue() == 5) {
					//競合する設定があれば削除
					if(!"1".equals(graphListRegisterForm.getEditRestrictionFlag()) && (!editFlag && newGraphList.getGraphTypeId() != null && editGraphTypeId.intValue() != newGraphList.getGraphTypeId().intValue())){
						try {
							graphListTemplateValRepository.deleteByGraphId(newGraphList.getGraphId());
						}catch(Exception e) {
							LOGGER.error(e.getMessage());
						}
					}
					Map<String,Integer> resultMap = new LinkedHashMap<>();
					Map<String,Integer> columnMap = graphListRegisterForm.getColumnMap();
					List<String> columnListPlaceHoderNameList = new ArrayList<>();
					columnMap.forEach((k,v)->{
						LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,k);
						if(v != null && v > 0 && layerSourceFieldForm != null) {
							String alias = layerSourceFieldForm.getAlias();
							alias = SQLUtil.aliasNameEscape(alias);
							if(alias != null) {
								resultMap.put(alias, v);
								columnListPlaceHoderNameList.add(" \"" + k + "\" as \"" + alias  + "\" ");
							}else {
								resultMap.put(k, v);
								columnListPlaceHoderNameList.add(" \"" + k + "\" ");
							}
						}
					});
					JSONObject json =  new JSONObject(resultMap);
					GraphListTemplateVal graphListTemplateVal = new GraphListTemplateVal();
					GraphListTemplateValPK graphListTemplateValPK = new GraphListTemplateValPK();
					graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
					graphListTemplateValPK.setItemId(2);
					graphListTemplateVal.setId(graphListTemplateValPK);
					graphListTemplateVal.setItemValue(json.toString());
					graphListTemplateValRepository.save(graphListTemplateVal);
					graphListTemplateVal = new GraphListTemplateVal();
					String columnListPlaceHoderName = String.join(",",columnListPlaceHoderNameList);
					graphListTemplateValPK = new GraphListTemplateValPK();
					graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
					graphListTemplateValPK.setItemId(100);
					graphListTemplateVal.setId(graphListTemplateValPK);
					graphListTemplateVal.setItemValue(columnListPlaceHoderName);
					graphListTemplateValRepository.save(graphListTemplateVal);
					graphListTemplateVal = new GraphListTemplateVal();
					graphListTemplateValPK = new GraphListTemplateValPK();
					graphListTemplateValPK.setGraphId(newGraphList.getGraphId());
					graphListTemplateValPK.setItemId(102);
					graphListTemplateVal.setId(graphListTemplateValPK);
					if(layerSourceForm.getTableName() != null) {
						graphListTemplateVal.setItemValue(layerSourceForm.getTableName());
						graphListTemplateValRepository.save(graphListTemplateVal);
					}
					
					result = newGraphList.getGraphId();					

				}else {
					throw new Exception("processing error");
				}
				
			}else {
				throw new Exception("processing error");
			}
			
		}else {
			throw new Exception("processing error");
		}
		
		LOGGER.info("グラフ・リスト情報の作成終了 graphTypeId:" + graphListRegisterForm.getGraphTypeId());
		
		//queryの検証を行う
		if(result != null) {
			try {
				checkGraphListData(result);
			}catch(Exception e) {
				throw new Exception("processing error");
			}
		}else {
			throw new Exception("processing error");
		}
		
		if(result!=null && !"1".equals(graphListRegisterForm.getEditRestrictionFlag())) {
			try {
				layerGraphCooporationRepository.deleteByGraphId(result);
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		if(result!=null && !"".equals(LonLatPlaceHoderNameReplaceText) && !"1".equals(graphListRegisterForm.getEditRestrictionFlag()) && !"1".equals(graphListRegisterForm.getGroupByFlag())) {
			LOGGER.info("レイヤグラフ連携の作成　開始 graphId:" + result);
			LayerGraphCooporation layerGraphCooporation = new LayerGraphCooporation();
			List<LayerGraphCooporation> layerGraphCooporationList = layerGraphCooporationRepository.findByGraphId(result);
			if(layerGraphCooporationList != null) {
				for(LayerGraphCooporation layerGraphCooporationTemp:layerGraphCooporationList) {
					if(layerGraphCooporationTemp.getLayerId() == layerSourceForm.getLayerId()) {
						layerGraphCooporation = layerGraphCooporationTemp;
					}
				}
			}
			layerGraphCooporation.setCooperationType(0);
			layerGraphCooporation.setGraphId(result);
			layerGraphCooporation.setLayerId(layerSourceForm.getLayerId());
			String cooperationOption = "{\"featureAttributeName\":\"_\",\"featureAttributeColumnName\":\"_\",\"longitudeColumnName\":\"longitude\",\"latitudeColumnName\":\"latitude\"}";
			layerGraphCooporation.setCooperationOption(cooperationOption);
			layerGraphCooporationRepository.save(layerGraphCooporation);
			LOGGER.info("レイヤグラフ連携の作成　終了 graphId:" + result);
		}
		
		return result;
	}
	
	/**
	 * グラフ・リスト情報のプレビュー(新規作成対象)
	 * 
	 * @param graphListRegisterForm
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm preview(GraphListRegisterForm graphListRegisterForm) throws Exception {
		LOGGER.info("グラフ・リスト情報のプレビュー開始 graphTypeId:" + graphListRegisterForm.getGraphTypeId());
		boolean editFlag = false;
		GraphListForm graphListForm = new GraphListForm();
		if(graphListRegisterForm.getGraphId() != null && graphListRegisterForm.getGraphId().intValue() > -1) {
			Optional<GraphList> graphListOpt = graphListRepository.findById(graphListRegisterForm.getGraphId());
			if(graphListOpt.isPresent()) {
				graphListForm = entityToForm(graphListOpt.get());
				editFlag = true;
			}
		}
		graphListForm.setEditFlag("1");
		graphListForm.setPlaceholderFlag("1");
		graphListForm.setGraphName(graphListRegisterForm.getGraphName());
		graphListForm.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
		graphListForm.setSourceId(graphListRegisterForm.getSourceId());
		LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphListRegisterForm.getSourceId());
		Optional<GraphListType> graphListTypeOpt = graphListTypeRepository.findById(graphListRegisterForm.getGraphTypeId());
		
		if(graphListTypeOpt.isPresent() && layerSourceForm != null && layerSourceForm.getTableName() != null) {
			
			String defaultQueryText = graphListTypeOpt.get().getDefaultQueryText();
			
			if(defaultQueryText != null && defaultQueryText != "") {
				
				if(!"1".equals(graphListRegisterForm.getEditRestrictionFlag())){
					//ソートモードを置き換え
					String sortMode = ""; 
					if(graphListRegisterForm.getSortModeMap() != null) {
						Map<String,String> sortModeMap = graphListRegisterForm.getSortModeMap();
						Iterator<String> iterator = sortModeMap.keySet().iterator();
						while(iterator.hasNext()) {
							String columnName = iterator.next();
							LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,columnName);
							if(layerSourceFieldForm != null) {
								String order = sortModeMap.get(columnName);
								if(order == null || "".equals(order)) {
									order = "ASC";
								}
								if(!"item_1".equals(columnName) && !"item_2".equals(columnName) && !"item_3".equals(columnName)
								  && !"item_4".equals(columnName) && !"item_5".equals(columnName) && !"item_6".equals(columnName)
								  && !"item_7".equals(columnName) && !"item_8".equals(columnName) && !"item_9".equals(columnName)
								  && !"item_10".equals(columnName)) {
									if(layerSourceFieldForm.getAlias() != null) {
										columnName = layerSourceFieldForm.getAlias();
										columnName = SQLUtil.aliasNameEscape(columnName);
										
									}
									sortMode = sortMode + " \""+columnName+"\" " + " "+order+" ,";
								}else {
									Integer itemId = Integer.parseInt(columnName.replace("item_", ""));
									PostLayerAttribute postLayerAttribute = postLayerAttributeRepository.findByLayerIdAndItemId(layerSourceForm.getLayerId(), itemId);
									Integer itemType = postLayerAttribute.getItemType();
									if(itemType.intValue() == 3 && (!"1".equals(graphListRegisterForm.getGroupByFlag()) || graphListRegisterForm.getGraphTypeId().intValue() == 5)) {
										sortMode = sortMode + " CASE WHEN isnumericex(cast(\"" + columnName + "\" as text)) THEN cast(\""+columnName+"\" as numeric) ELSE 0 END "+order+" ,";
									}else {
										if(layerSourceFieldForm.getAlias() != null) {
											columnName = layerSourceFieldForm.getAlias();
											columnName = SQLUtil.aliasNameEscape(columnName);
											
										}
										sortMode = sortMode + " \""+columnName+"\" " + " "+order+" ,";
									}
								}
							}
						}
					}
					if(sortMode.length() > 0) {
						sortMode = sortMode.substring(0, sortMode.length()-1);
					} else {
						defaultQueryText = defaultQueryText.replaceAll("\\QORDER BY\\E", "");
					}
					defaultQueryText = defaultQueryText.replaceAll("\\Q{{SortMode}}\\E", SQLUtil.sqlEscape(sortMode));
					
					//リミット数を置き換え（TODO:デフォルト数は外だし予定）
					Integer limitSize = graphListRegisterForm.getLimitSize();
					if(limitSize == null) {
						limitSize = 15;
					}
					defaultQueryText = defaultQueryText.replaceAll("\\Q{{LimitSize}}\\E", SQLUtil.sqlEscape(limitSize + ""));
					
					//経度・緯度のカラムを置き換え　geometry
					String LonLatPlaceHoderNameReplaceText = "";
					String geomColumnName = "";
					//集約関数有の場合geomは対象外にする
					if(!"1".equals(graphListRegisterForm.getGroupByFlag())) {
						List<Map<String, Object>> resultListForGeom = executeQueryDao.executeQuery("select table_name,column_name from information_schema.columns where column_name = 'geom' OR column_name = 'geometry';");
						if(resultListForGeom != null) {
							for(Map<String, Object> resultMap:resultListForGeom) {
								String tableName = (String) resultMap.get("table_name");
								if(tableName != null && layerSourceForm.getTableName() !=null && layerSourceForm.getTableName().equals(tableName)) {
									geomColumnName = (String) resultMap.get("column_name");
									LonLatPlaceHoderNameReplaceText = LonLatPlaceHoderNameReplaceText + " ((ST_XMax(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326))+ST_XMin(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326)))/2) AS longitude ";
									LonLatPlaceHoderNameReplaceText = LonLatPlaceHoderNameReplaceText + " , ((ST_YMax(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326))+ST_YMin(ST_TransForm("+SQLUtil.sqlEscape(geomColumnName)+", 4326)))/2) AS latitude ";
								}
							}
						}
					}
					if(!"".equals(LonLatPlaceHoderNameReplaceText)) {
						defaultQueryText = defaultQueryText.replaceAll("\\Q{{LonLatPlaceHoderName}}\\E", SQLUtil.sqlEscape(LonLatPlaceHoderNameReplaceText));
					}else {
						defaultQueryText = defaultQueryText.replaceAll("\\Q,{{LonLatPlaceHoderName}}\\E", "");
					}
					
					//WHERE条件(公開フラグがセットされている場合は条件付与)
					String conditionsForPublishFlag = "";
					List<Map<String, Object>> resultListForPublishFlag = executeQueryDao.executeQuery("select table_name,column_name from information_schema.columns where column_name = 'publish_flag';");
					if(resultListForPublishFlag != null) {
						for(Map<String, Object> resultMap:resultListForPublishFlag) {
							String tableName = (String) resultMap.get("table_name");
							if(tableName != null && layerSourceForm.getTableName() !=null && layerSourceForm.getTableName().equals(tableName)) {
								conditionsForPublishFlag = " \"publish_flag\" = '1' ";
							}
						}
					}
					if(graphListRegisterForm.getGraphYColumn() != null && !"".equals(graphListRegisterForm.getGraphYColumn())) {
						if("".equals(conditionsForPublishFlag)) {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{YColumnPlaceHoderName}}\\E", "\"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphYColumn()) + "\"" );
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{YColumnPlaceHoderName}}\\E", conditionsForPublishFlag + " AND "+"\"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphYColumn()) + "\"" );
						}
					}else {
						if("".equals(conditionsForPublishFlag)) {
							defaultQueryText = defaultQueryText.replaceAll("\\QWHERE {{YColumnPlaceHoderName}} IS NOT NULL\\E", "");
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{YColumnPlaceHoderName}} IS NOT NULL\\E", conditionsForPublishFlag);
						}
					}
					//リストの場合の考慮
					if(graphListRegisterForm.getGraphTypeId().intValue() == 5) {
						if("".equals(conditionsForPublishFlag)) {
							defaultQueryText = defaultQueryText.replaceAll("\\QWHERE {{ConditionsForPublishFlag}}\\E", "");
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{ConditionsForPublishFlag}}\\E", conditionsForPublishFlag);
						}
					}
					
					//GROUP BY 条件
					if("1".equals(graphListRegisterForm.getGroupByFlag()) && graphListRegisterForm.getAggregationType() != null && graphListRegisterForm.getAggregationType().intValue() > 0 &&
							(graphListRegisterForm.getGraphTypeId().intValue() == 2 
							|| graphListRegisterForm.getGraphTypeId().intValue() == 3 
								|| graphListRegisterForm.getGraphTypeId().intValue()== 4)) {
						if(!"".equals(geomColumnName)) {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{GroupBy}}\\E", " GROUP BY \"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphXColumn()) + "\",\""+geomColumnName+"\" " );
						}else {
							defaultQueryText = defaultQueryText.replaceAll("\\Q{{GroupBy}}\\E", " GROUP BY \"" + SQLUtil.sqlEscape(graphListRegisterForm.getGraphXColumn()) + "\" " );
						}
						String itemValue = graphListRegisterForm.getGraphYColumn();
						LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
						if(layerSourceFieldForm != null) {
							String alias = layerSourceFieldForm.getAlias();
							alias = SQLUtil.aliasNameEscape(alias);
							switch(graphListRegisterForm.getAggregationType()) {
								case 1:
									if(alias != null) {
										itemValue = " SUM(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " SUM(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 2:
									if(alias != null) {
										itemValue = " AVG(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " AVG(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 3:
									if(alias != null) {
										itemValue = " MIN(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " MIN(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 4:
									if(alias != null) {
										itemValue = " MAX(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " MAX(CASE WHEN isnumericex(cast(\"" + SQLUtil.sqlEscape(itemValue) + "\" as text)) THEN cast(\""+SQLUtil.sqlEscape(itemValue)+"\" as numeric) ELSE 0 END) as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								case 5:
									if(alias != null) {
										itemValue = " COUNT(\"" + SQLUtil.sqlEscape(itemValue) + "\") as \"" + SQLUtil.sqlEscape(alias) + "\" ";
									}else {
										itemValue = " COUNT(\"" + SQLUtil.sqlEscape(itemValue) + "\") as \"" + SQLUtil.sqlEscape(itemValue) + "\" ";
									}
									break;
								default:
									break;
							}
							defaultQueryText = defaultQueryText.replaceAll("\\Q$YColumnPlaceHoderName$\\E", itemValue);
						}
					}else {
						defaultQueryText = defaultQueryText.replaceAll("\\Q{{GroupBy}}\\E", "" );
					}
				}
				
				//テンプレート項目値の設定
				//円グラフ or 棒グラフ or 線グラフの場合
				List<GraphListTemplateValForm> graphListTemplateValFormList = new ArrayList<>();
				if(editFlag) {
					graphListTemplateValFormList = graphListForm.getGraphListTemplateValFormList();
				}
				if(graphListRegisterForm.getGraphTypeId().intValue() == 2 
						|| graphListRegisterForm.getGraphTypeId().intValue() == 3 
							|| graphListRegisterForm.getGraphTypeId().intValue() == 4) {
					if(editFlag) {
						graphListTemplateValFormList.removeIf(graphListTemplateValForm->graphListTemplateValForm.getItemId().intValue()==1||graphListTemplateValForm.getItemId().intValue()==2||graphListTemplateValForm.getItemId().intValue()==11||graphListTemplateValForm.getItemId().intValue()==100||graphListTemplateValForm.getItemId().intValue()==101||graphListTemplateValForm.getItemId().intValue()==102);
					}
					GraphListTemplateValForm graphListTemplateValForm = new GraphListTemplateValForm();
					GraphListTemplateSettingPK graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(1);
					graphListTemplateValForm.setItemId(1);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					String itemValue = graphListRegisterForm.getGraphYColumn();
					String yColumnName = itemValue;
					LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
					if(layerSourceFieldForm != null) {
						String alias = layerSourceFieldForm.getAlias();
						alias = SQLUtil.aliasNameEscape(alias);
						if(alias != null) {
							itemValue = " \"" + itemValue + "\" as \"" + alias + "\" ";
							yColumnName = alias;
						}
						graphListTemplateValForm.setItemValue(yColumnName);
						graphListTemplateValFormList.add(graphListTemplateValForm);
						defaultQueryText = defaultQueryText.replaceAll("\\Q$YColumnPlaceHoderName$\\E", SQLUtil.sqlEscape(itemValue));
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(100);
						graphListTemplateValForm.setItemId(100);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(itemValue);
						graphListTemplateValFormList.add(graphListTemplateValForm);		
					}
					
					graphListTemplateValForm = new GraphListTemplateValForm();
					graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(2);
					graphListTemplateValForm.setItemId(2);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					itemValue = graphListRegisterForm.getGraphXColumn();
					String xColumnName = itemValue;
					layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
					if(layerSourceFieldForm != null) {
						String alias = layerSourceFieldForm.getAlias();
						alias = SQLUtil.aliasNameEscape(alias);
						if(alias != null) {
							itemValue = " \"" + itemValue + "\" as \"" + alias + "\" ";
							xColumnName = alias;
						}
						graphListTemplateValForm.setItemValue(xColumnName);
						graphListTemplateValFormList.add(graphListTemplateValForm);
						defaultQueryText = defaultQueryText.replaceAll("\\Q$XColumnPlaceHoderName$\\E", SQLUtil.sqlEscape(itemValue));
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(101);
						graphListTemplateValForm.setItemId(101);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(itemValue);
						graphListTemplateValFormList.add(graphListTemplateValForm);		
					};
					if(graphListRegisterForm.getGraphTypeId().intValue() == 3 && "horizontal".equals(graphListRegisterForm.getGraphDirection())){
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(11);
						graphListTemplateValForm.setItemId(11);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(graphListRegisterForm.getGraphDirection());
						graphListTemplateValFormList.add(graphListTemplateValForm);
					}
					if(layerSourceForm.getTableName() != null) {
						defaultQueryText = defaultQueryText.replaceAll("\\Q$TablePlaceHoderName$\\E", SQLUtil.sqlEscape(layerSourceForm.getTableName()));
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(102);
						graphListTemplateValForm.setItemId(102);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(layerSourceForm.getTableName());
						graphListTemplateValFormList.add(graphListTemplateValForm);		
					}
					
				//リストの場合
				}else if(graphListRegisterForm.getGraphTypeId().intValue() == 5) {
					if(editFlag) {
						graphListTemplateValFormList.removeIf(graphListTemplateValForm->graphListTemplateValForm.getItemId().intValue()==2);
					}
					GraphListTemplateValForm graphListTemplateValForm = new GraphListTemplateValForm();
					GraphListTemplateSettingPK graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(2);
					graphListTemplateValForm.setItemId(2);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					
					Map<String,Integer> resultMap = new LinkedHashMap<>();
					Map<String,Integer> columnMap = graphListRegisterForm.getColumnMap();
					List<String> columnListPlaceHoderNameList = new ArrayList<>();
					columnMap.forEach((k,v)->{
						LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,k);
						if(v != null && v > 0 && layerSourceFieldForm != null) {
							String alias = layerSourceFieldForm.getAlias();
							alias = SQLUtil.aliasNameEscape(alias);
							if(alias != null) {
								resultMap.put(alias, v);
								columnListPlaceHoderNameList.add(" \"" + k + "\" as \"" + alias  + "\" ");
							}else {
								resultMap.put(k, v);
								columnListPlaceHoderNameList.add(" \"" + k + "\" ");
							}
						}
					});
					
					JSONObject json =  new JSONObject(resultMap);
					graphListTemplateValForm.setItemValue(json.toString());
					graphListTemplateValFormList.add(graphListTemplateValForm);
					
					String columnListPlaceHoderName = String.join(",",columnListPlaceHoderNameList);
					defaultQueryText = defaultQueryText.replaceAll("\\Q$ColumnListPlaceHoderName$\\E",SQLUtil.sqlEscape(columnListPlaceHoderName));
					
					if(layerSourceForm.getTableName() != null) {
						defaultQueryText = defaultQueryText.replaceAll("\\Q$TablePlaceHoderName$\\E",SQLUtil.sqlEscape(layerSourceForm.getTableName()));
					}
					
					graphListTemplateValForm = new GraphListTemplateValForm();
					graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(100);
					graphListTemplateValForm.setItemId(100);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					graphListTemplateValForm.setItemValue(columnListPlaceHoderName);
					graphListTemplateValFormList.add(graphListTemplateValForm);
					graphListTemplateValForm = new GraphListTemplateValForm();
					graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(102);
					graphListTemplateValForm.setItemId(102);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					graphListTemplateValForm.setItemValue(layerSourceForm.getTableName());
					graphListTemplateValFormList.add(graphListTemplateValForm);		

				}else {
					throw new Exception("processing error");
				}
				LOGGER.info("実行query:" + defaultQueryText);
				List<Map<String, Object>> resultList = executeQueryDao.executeQuery(defaultQueryText);
				graphListForm.setQueryText(defaultQueryText);
				graphListForm.setDataList(resultList);
				graphListForm.setGraphListTemplateValFormList(graphListTemplateValFormList);
				
			}else {
				throw new Exception("processing error");
			}
			
		}else {
			throw new Exception("processing error");
		}
		LOGGER.info("グラフ・リスト情報のプレビュー終了 graphTypeId:" + graphListRegisterForm.getGraphTypeId());
		return graphListForm;
	}
	
	/**
	 * グラフ・リスト情報のプレビュー(編集制限の場合)
	 * 
	 * @param graphListRegisterForm
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm editRestrictionPreview(GraphListRegisterForm graphListRegisterForm) throws Exception {
		LOGGER.info("グラフ・リスト情報のプレビュー開始 graphTypeId:" + graphListRegisterForm.getGraphTypeId());
		GraphListForm graphListForm = new GraphListForm();
		Optional<GraphList> graphListOpt = graphListRepository.findById(graphListRegisterForm.getGraphId());
		if(graphListOpt.isPresent()) {
			graphListForm = entityToForm(graphListOpt.get());
			graphListForm.setQueryText(graphListOpt.get().getQueryText());
		}
		LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphListRegisterForm.getSourceId());
		Optional<GraphListType> graphListTypeOpt = graphListTypeRepository.findById(graphListRegisterForm.getGraphTypeId());
		
		if(graphListTypeOpt.isPresent() && layerSourceForm != null && layerSourceForm.getTableName() != null) {
			
			String defaultQueryText = graphListForm.getQueryText();
			
			if(defaultQueryText != null && defaultQueryText != "") {
				
				//テンプレート項目値の設定
				//円グラフ or 棒グラフ or 線グラフの場合
				List<GraphListTemplateValForm> graphListTemplateValFormList = graphListForm.getGraphListTemplateValFormList();
				if(graphListRegisterForm.getGraphTypeId().intValue() == 2 
						|| graphListRegisterForm.getGraphTypeId().intValue() == 3 
							|| graphListRegisterForm.getGraphTypeId().intValue() == 4) {
					
					graphListTemplateValFormList.removeIf(graphListTemplateValForm->graphListTemplateValForm.getItemId().intValue()==1||graphListTemplateValForm.getItemId().intValue()==2||graphListTemplateValForm.getItemId().intValue()==11||graphListTemplateValForm.getItemId().intValue()==100||graphListTemplateValForm.getItemId().intValue()==101||graphListTemplateValForm.getItemId().intValue()==102);

					GraphListTemplateValForm graphListTemplateValForm = new GraphListTemplateValForm();
					GraphListTemplateSettingPK graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(1);
					graphListTemplateValForm.setItemId(1);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					String itemValue = graphListRegisterForm.getGraphYColumn();
					String yColumnName = itemValue;
					LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
					if(layerSourceFieldForm != null) {
						String alias = layerSourceFieldForm.getAlias();
						alias = SQLUtil.aliasNameEscape(alias);
						if(alias != null) {
							itemValue = " \"" + itemValue + "\" as \"" + alias + "\" ";
							yColumnName = alias;
						}
						graphListTemplateValForm.setItemValue(yColumnName);
						graphListTemplateValFormList.add(0,graphListTemplateValForm);
						defaultQueryText = defaultQueryText.replaceAll("\\Q$YColumnPlaceHoderName$\\E", SQLUtil.sqlEscape(itemValue));
						//プレースホルダのカラム置き換えでFROM以降のas句は取り除く
						//簡易のSQLを動的queryの対象とする
						defaultQueryText = defaultQueryText.replaceAll("[\r\n]", "");
						if(itemValue.toLowerCase().contains(" as ")) {
							String replaceSubStringText = itemValue.substring(0,itemValue.toLowerCase().indexOf(" as "));
							String beforeQueryText = defaultQueryText.substring(0,defaultQueryText.toLowerCase().indexOf(" from "));
							String afterQueryText = defaultQueryText.substring(defaultQueryText.toLowerCase().indexOf(" from ")+1);
							afterQueryText = afterQueryText.replaceAll("\\Q" + itemValue + "\\E", SQLUtil.sqlEscape(replaceSubStringText));
							defaultQueryText = beforeQueryText + " " + afterQueryText;
						}
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(100);
						graphListTemplateValForm.setItemId(100);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(itemValue);
						graphListTemplateValFormList.add(0,graphListTemplateValForm);			
					}
					
					graphListTemplateValForm = new GraphListTemplateValForm();
					graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(2);
					graphListTemplateValForm.setItemId(2);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					itemValue = graphListRegisterForm.getGraphXColumn();
					String xColumnName = itemValue;
					layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,itemValue);
					if(layerSourceFieldForm != null) {
						String alias = layerSourceFieldForm.getAlias();
						alias = SQLUtil.aliasNameEscape(alias);
						if(alias != null) {
							itemValue = " \"" + itemValue + "\" as \"" + alias + "\" ";
							xColumnName = alias;
						}
						graphListTemplateValForm.setItemValue(xColumnName);
						graphListTemplateValFormList.add(0,graphListTemplateValForm);
						defaultQueryText = defaultQueryText.replaceAll("\\Q$XColumnPlaceHoderName$\\E", SQLUtil.sqlEscape(itemValue));
						//プレースホルダのカラム置き換えでFROM以降のas句は取り除く
						//簡易のSQLを動的queryの対象とする
						defaultQueryText = defaultQueryText.replaceAll("[\r\n]", "");
						if(itemValue.toLowerCase().contains(" as ")) {
							String replaceSubStringText = itemValue.substring(0,itemValue.toLowerCase().indexOf(" as "));
							String beforeQueryText = defaultQueryText.substring(0,defaultQueryText.toLowerCase().indexOf(" from "));
							String afterQueryText = defaultQueryText.substring(defaultQueryText.toLowerCase().indexOf(" from ")+1);
							afterQueryText = afterQueryText.replaceAll("\\Q" + itemValue + "\\E", SQLUtil.sqlEscape(replaceSubStringText));
							defaultQueryText = beforeQueryText + " " + afterQueryText;
						}
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(101);
						graphListTemplateValForm.setItemId(101);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(itemValue);
						graphListTemplateValFormList.add(0,graphListTemplateValForm);
					};
					if(graphListRegisterForm.getGraphTypeId().intValue() == 3 && "horizontal".equals(graphListRegisterForm.getGraphDirection())){
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(11);
						graphListTemplateValForm.setItemId(11);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(graphListRegisterForm.getGraphDirection());
						graphListTemplateValFormList.add(0,graphListTemplateValForm);
					}
					if(layerSourceForm.getTableName() != null) {
						defaultQueryText = defaultQueryText.replaceAll("\\Q$TablePlaceHoderName$\\E", SQLUtil.sqlEscape(layerSourceForm.getTableName()));
						graphListTemplateValForm = new GraphListTemplateValForm();
						graphListTemplateSettingPK = new GraphListTemplateSettingPK();
						graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
						graphListTemplateSettingPK.setItemId(102);
						graphListTemplateValForm.setItemId(102);
						graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
						graphListTemplateValForm.setItemValue(layerSourceForm.getTableName());
						graphListTemplateValFormList.add(0,graphListTemplateValForm);
					}
					
				//リストの場合
				}else if(graphListRegisterForm.getGraphTypeId().intValue() == 5) {
					
					graphListTemplateValFormList.removeIf(graphListTemplateValForm->graphListTemplateValForm.getItemId().intValue()==2);
					
					GraphListTemplateValForm graphListTemplateValForm = new GraphListTemplateValForm();
					GraphListTemplateSettingPK graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(2);
					graphListTemplateValForm.setItemId(2);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					
					Map<String,Integer> resultMap = new LinkedHashMap<>();
					Map<String,Integer> columnMap = graphListRegisterForm.getColumnMap();
					List<String> columnListPlaceHoderNameList = new ArrayList<>();
					columnMap.forEach((k,v)->{
						LayerSourceFieldForm layerSourceFieldForm = layerSourceColumnPresentCheck(layerSourceForm,k);
						if(v != null && v > 0 && layerSourceFieldForm != null) {
							String alias = layerSourceFieldForm.getAlias();
							alias = SQLUtil.aliasNameEscape(alias);
							if(alias != null) {
								resultMap.put(alias, v);
								columnListPlaceHoderNameList.add(" \"" + k + "\" as \"" + alias  + "\" ");
							}else {
								resultMap.put(k, v);
								columnListPlaceHoderNameList.add(" \"" + k + "\" ");
							}
						}
					});
					
					JSONObject json =  new JSONObject(resultMap);
					graphListTemplateValForm.setItemValue(json.toString());
					graphListTemplateValFormList.add(0,graphListTemplateValForm);
					
					String columnListPlaceHoderName = String.join(",",columnListPlaceHoderNameList);
					defaultQueryText = defaultQueryText.replaceAll("\\Q$ColumnListPlaceHoderName$\\E", SQLUtil.sqlEscape(columnListPlaceHoderName));
					
					if(layerSourceForm.getTableName() != null) {
						defaultQueryText = defaultQueryText.replaceAll("\\Q$TablePlaceHoderName$\\E", SQLUtil.sqlEscape(layerSourceForm.getTableName()));
					}
					
					graphListTemplateValForm = new GraphListTemplateValForm();
					graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(100);
					graphListTemplateValForm.setItemId(100);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					graphListTemplateValForm.setItemValue(columnListPlaceHoderName);
					graphListTemplateValFormList.add(0,graphListTemplateValForm);
					graphListTemplateValForm = new GraphListTemplateValForm();
					graphListTemplateSettingPK = new GraphListTemplateSettingPK();
					graphListTemplateSettingPK.setGraphTypeId(graphListRegisterForm.getGraphTypeId());
					graphListTemplateSettingPK.setItemId(102);
					graphListTemplateValForm.setItemId(102);
					graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingService.findById(graphListTemplateSettingPK));
					graphListTemplateValForm.setItemValue(layerSourceForm.getTableName());
					graphListTemplateValFormList.add(0,graphListTemplateValForm);	

				}else {
					throw new Exception("processing error");
				}
				
				List<ThemeForm> themeFormList = themeService.findAll();
				
				for(ThemeForm themeForm:themeFormList) {
					//切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
					if(themeForm != null && "1".equals(themeForm.getSwitchFlag()) && defaultQueryText != null && themeForm.getSwitchPlaceholderName() != null && themeForm.getSwitchPlaceholderDefaultValue() != null) {
						String replaceText = themeForm.getSwitchPlaceholderDefaultValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						defaultQueryText = defaultQueryText.replaceAll("\\Q" + themeForm.getSwitchPlaceholderName() + "\\E", replaceText);
					}
				}
				
				List<Map<String, Object>> resultList = executeQueryDao.executeQuery(defaultQueryText);
				graphListForm.setPreviewFlag("1");
				graphListForm.setDataList(resultList);
				graphListForm.setGraphListTemplateValFormList(graphListTemplateValFormList);
				
			}else {
				throw new Exception("processing error");
			}
			
		}else {
			throw new Exception("processing error");
		}
		LOGGER.info("グラフ・リスト情報のプレビュー終了 graphTypeId:" + graphListRegisterForm.getGraphTypeId());
		return graphListForm;
	}
	
	/**
	 * グラフ・リスト情報の削除
	 * 
	 * @param graphListId グラフリストID
	 * @return result 成功=true 失敗=false
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteByGraphListIdForAdmin(Integer graphListId) throws Exception {
		boolean result = false;
		LOGGER.info("グラフ・リスト情報の削除開始 graphListId:" + graphListId);
		try {
			graphListTemplateValRepository.deleteByGraphId(graphListId);
			layerGraphCooporationRepository.deleteByGraphId(graphListId);
			themeGraphListRepository.deleteByGraphId(graphListId);
			graphListRepository.deleteByGraphId(graphListId);
			result = true;
		}catch(Exception e) {
			throw new Exception("processing error");
		}
		LOGGER.info("グラフ・リスト情報の削除開始 graphListId:" + graphListId);
		if(!result) {
			throw new Exception("processing error");
		}
		return result;
	}
	
	/**
	 * レイヤソースフィールド更新時のグラフ・リスト情報のエイリアス名を更新
	 * 
	 * @param sourceId sauceID
	 * @return result 成功=true 失敗=false
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateGraphListForLayerSourceField(Integer sourceId, List<PostLayerAttributeForm> postLayerAttributeFormList) throws Exception {
		boolean result = false;
		LOGGER.info("グラフ・リスト情報の更新開始 sourceId:" + sourceId);
		try {
			List<GraphList> graphListList = graphListRepository.findBySourceId(sourceId);
			if(graphListList != null && graphListList.size() > 0) {
				for(GraphList graphList: graphListList) {
					if("1".equals(graphList.getEditFlag())) {
						//グラフリストのquery内のレイヤソースフィールドを更新する
						String queryText = graphList.getQueryText();
						for(PostLayerAttributeForm postLayerAttributeForm: postLayerAttributeFormList) {
							if(postLayerAttributeForm.getItemId() != null && postLayerAttributeForm.getItemId().intValue() >= 1 && postLayerAttributeForm.getItemId().intValue() <= 10) {
								LayerSourceField layerSourceField = layerSourceFieldRepository.findBySourceIdAndFieldName(sourceId, "item_"+postLayerAttributeForm.getItemId().toString());
								if(layerSourceField != null && layerSourceField.getFieldId() != null) {
									String beforeAlias = layerSourceField.getAlias();
									String newAlias = postLayerAttributeForm.getItemName();
									if(newAlias != null && beforeAlias != null) {
										newAlias = SQLUtil.aliasNameEscape(newAlias);
										newAlias = SQLUtil.sqlEscape(newAlias);
										beforeAlias = SQLUtil.aliasNameEscape(beforeAlias);
										beforeAlias = SQLUtil.sqlEscape(beforeAlias);
										queryText = queryText.replaceAll("\\Q\"" + beforeAlias + "\"\\E", "\"" + newAlias + "\"");
									}
								}
							}
						}
						graphList.setQueryText(queryText);
						graphListRepository.save(graphList);
						
						//グラフリストの設定項目値内のレイヤソースフィールドを更新する
						List<GraphListTemplateVal> graphListTemplateValList = graphListTemplateValRepository.findByGraphId(graphList.getGraphId());
						if(graphListTemplateValList != null && graphListTemplateValList.size() > 0) {
							for(GraphListTemplateVal graphListTemplateVal: graphListTemplateValList) {
								for(PostLayerAttributeForm postLayerAttributeForm: postLayerAttributeFormList) {
									if(postLayerAttributeForm.getItemId() != null && postLayerAttributeForm.getItemId().intValue() >= 1 && postLayerAttributeForm.getItemId().intValue() <= 10) {
										//円グラフ or 棒グラフ or 線グラフの場合
										if(graphList.getGraphTypeId().intValue() == 2 
												|| graphList.getGraphTypeId().intValue() == 3 
													|| graphList.getGraphTypeId().intValue() == 4) {
											if(graphListTemplateVal.getId().getItemId().intValue() == 1 || graphListTemplateVal.getId().getItemId().intValue() == 2) {
												LayerSourceField layerSourceField = layerSourceFieldRepository.findBySourceIdAndFieldName(sourceId, "item_"+postLayerAttributeForm.getItemId().toString());
												if(layerSourceField != null && layerSourceField.getFieldId() != null) {
													String itemValue = graphListTemplateVal.getItemValue();
													String beforeAlias = layerSourceField.getAlias();
													String newAlias = postLayerAttributeForm.getItemName();
													beforeAlias = SQLUtil.aliasNameEscape(beforeAlias);
													beforeAlias = SQLUtil.sqlEscape(beforeAlias);
													newAlias = SQLUtil.aliasNameEscape(newAlias);
													newAlias = SQLUtil.sqlEscape(newAlias);
													if(itemValue != null && itemValue.equals(beforeAlias)) {
														graphListTemplateVal.setItemValue(newAlias);
														graphListTemplateValRepository.save(graphListTemplateVal);
													}
												}
											}else if(graphListTemplateVal.getId().getItemId().intValue() == 100 || graphListTemplateVal.getId().getItemId().intValue() == 101) {
												LayerSourceField layerSourceField = layerSourceFieldRepository.findBySourceIdAndFieldName(sourceId, "item_"+postLayerAttributeForm.getItemId().toString());
												if(layerSourceField != null && layerSourceField.getFieldId() != null) {
													String itemValue = graphListTemplateVal.getItemValue();
													String beforeAlias = layerSourceField.getAlias();
													String newAlias = postLayerAttributeForm.getItemName();
													beforeAlias = SQLUtil.aliasNameEscape(beforeAlias);
													beforeAlias = SQLUtil.sqlEscape(beforeAlias);
													newAlias = SQLUtil.aliasNameEscape(newAlias);
													newAlias = SQLUtil.sqlEscape(newAlias);
													itemValue = itemValue.replaceAll("\\Q\"" + beforeAlias + "\"\\E", "\"" + newAlias + "\"");
													graphListTemplateVal.setItemValue(itemValue);
													graphListTemplateValRepository.save(graphListTemplateVal);
												}
											}
										//リストの場合
										}else if(graphList.getGraphTypeId().intValue() == 5) {
											if(graphListTemplateVal.getId().getItemId().intValue() == 2 || graphListTemplateVal.getId().getItemId().intValue() == 100) {
												LayerSourceField layerSourceField = layerSourceFieldRepository.findBySourceIdAndFieldName(sourceId, "item_"+postLayerAttributeForm.getItemId().toString());
												if(layerSourceField != null && layerSourceField.getFieldId() != null) {
													String itemValue = graphListTemplateVal.getItemValue();
													String beforeAlias = layerSourceField.getAlias();
													String newAlias = postLayerAttributeForm.getItemName();
													beforeAlias = SQLUtil.aliasNameEscape(beforeAlias);
													beforeAlias = SQLUtil.sqlEscape(beforeAlias);
													newAlias = SQLUtil.aliasNameEscape(newAlias);
													newAlias = SQLUtil.sqlEscape(newAlias);
													itemValue = itemValue.replaceAll("\\Q\"" + beforeAlias + "\"\\E", "\"" + newAlias + "\"");
													graphListTemplateVal.setItemValue(itemValue);
													graphListTemplateValRepository.save(graphListTemplateVal);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			result = true;
		}catch(Exception e) {
			throw new Exception("processing error");
		}
		LOGGER.info("グラフ・リスト情報の更新終了 sourceId:" + sourceId);
		if(!result) {
			throw new Exception("processing error");
		}
		return result;
	}
	
	/**
	 * レイヤソースのカラム存在確認
	 * 
	 * @param layerSourceForm
	 * @param columnName
	 * @return LayerSourceFieldForm
	 */
	public LayerSourceFieldForm layerSourceColumnPresentCheck(LayerSourceForm layerSourceForm,String columnName) {
		LayerSourceFieldForm result = null;
		try {
			List<LayerSourceFieldForm> layerSourceFieldFormList = layerSourceForm.getLayerSourceFieldFormList();
			for(LayerSourceFieldForm layerSourceFieldForm : layerSourceFieldFormList) {
				if(layerSourceFieldForm != null && layerSourceFieldForm.getFieldName() != null &&
						layerSourceFieldForm.getFieldName().equals(columnName)) {
					result = layerSourceFieldForm;
					break;
				}
			}
		}catch(Exception e) {
			result = null;
			LOGGER.error(e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * entity→form詰め替え(グラフ・リストデータなし)
	 * 
	 * @param graphList グラフリストEntity
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm entityToFormNoDataList(GraphList graphList) {
		GraphListForm graphListForm = new GraphListForm();
		if(graphList != null) {
			graphListForm.setGraphId(graphList.getGraphId());
			graphListForm.setGraphTypeId(graphList.getGraphTypeId());
			graphListForm.setGraphName(graphList.getGraphName());
			graphListForm.setPlaceholderFlag(graphList.getPlaceholderFlag());
			try {
				String queryText = graphList.getQueryText();
				if(queryText != null && queryText.contains("_auto_query_identifier_")) {
					graphListForm.setDeleteFlag("1");
				}
			}catch(Exception e) {}
			List<GraphListTemplateValForm> graphListTemplateValFormList = graphListTemplateValService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setGraphListTemplateValFormList(graphListTemplateValFormList);
			graphListForm.setEditFlag(graphList.getEditFlag());
			graphListForm.setSourceId(graphList.getSourceId());
			List<LayerGraphCooporationForm> layerGraphCooporationFormList = layerGraphCooporationService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setLayerGraphCooporationFormList(layerGraphCooporationFormList);
			if(graphList.getSourceId() != null) {
				LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphList.getSourceId());
				graphListForm.setLayerSourceForm(layerSourceForm);
			}
		}
		return graphListForm;
	}
	
	/**
	 * entity→form詰め替え(グラフ・リストデータなし)
	 * 
	 * @param graphListList グラフリストEntityリスト
	 * @return graphListFormList グラフリストFormリスト
	 */
	public List<GraphListForm> entityToFormNoDataList(List<GraphList> graphListList) {
		List<GraphListForm> graphListFormList = new ArrayList<GraphListForm>();
		for(GraphList graphList : graphListList) {
			GraphListForm graphListForm = new GraphListForm();
			graphListForm.setGraphId(graphList.getGraphId());
			graphListForm.setGraphTypeId(graphList.getGraphTypeId());
			graphListForm.setGraphName(graphList.getGraphName());
			graphListForm.setPlaceholderFlag(graphList.getPlaceholderFlag());
			try {
				String queryText = graphList.getQueryText();
				if(queryText != null && queryText.contains("_auto_query_identifier_")) {
					graphListForm.setDeleteFlag("1");
				}
			}catch(Exception e) {}
			List<GraphListTemplateValForm> graphListTemplateValFormList = graphListTemplateValService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setGraphListTemplateValFormList(graphListTemplateValFormList);
			graphListForm.setEditFlag(graphList.getEditFlag());
			graphListForm.setSourceId(graphList.getSourceId());
			List<LayerGraphCooporationForm> layerGraphCooporationFormList = layerGraphCooporationService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setLayerGraphCooporationFormList(layerGraphCooporationFormList);
			graphListFormList.add(graphListForm);
			if(graphList.getSourceId() != null) {
				LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphList.getSourceId());
				graphListForm.setLayerSourceForm(layerSourceForm);
			}
		}
		return graphListFormList;
	}
	
	
	/**
	 * entity→form詰め替え（query_textの切替項目値をテーマのデフォルトプレースホルダ値で置き換え）
	 * 
	 * @param graphList グラフリストEntity
	 * @param themeId テーマId
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm entityToForm(GraphList graphList) {
		GraphListForm graphListForm = new GraphListForm();
		if(graphList != null) {
			graphListForm.setGraphId(graphList.getGraphId());
			graphListForm.setGraphTypeId(graphList.getGraphTypeId());
			graphListForm.setGraphName(graphList.getGraphName());
			graphListForm.setPlaceholderFlag(graphList.getPlaceholderFlag());
			try {
				String queryText = graphList.getQueryText();
				if(queryText != null && queryText.contains("_auto_query_identifier_")) {
					graphListForm.setDeleteFlag("1");
				}
			}catch(Exception e) {}
			List<GraphListTemplateValForm> graphListTemplateValFormList = graphListTemplateValService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setGraphListTemplateValFormList(graphListTemplateValFormList);
			try {
				List<Map<String, Object>> resultList = null;
				String queryText = graphList.getQueryText();
				List<ThemeForm> themeFormList = themeService.findAll();
				for(ThemeForm themeForm:themeFormList) {
					//切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
					if(themeForm != null && "1".equals(themeForm.getSwitchFlag()) && queryText != null && themeForm.getSwitchPlaceholderName() != null && themeForm.getSwitchPlaceholderDefaultValue() != null) {
						String replaceText = themeForm.getSwitchPlaceholderDefaultValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						queryText = queryText.replaceAll("\\Q" + themeForm.getSwitchPlaceholderName() + "\\E", replaceText);
					}
				}
				//プレースホルダのカラムを置き換える( 形式：$placeholeder_name$ )
				for(GraphListTemplateValForm graphListTemplateValForm:graphListTemplateValFormList) {
					if(graphListTemplateValForm!=null && graphListTemplateValForm.getItemValue() != null && graphListTemplateValForm.getGraphListTemplateSettingForm() != null && "1".equals(graphListTemplateValForm.getGraphListTemplateSettingForm().getPlaceholderFlag())) {
						String replaceText = graphListTemplateValForm.getItemValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						queryText = queryText.replaceAll("\\Q" + graphListTemplateValForm.getGraphListTemplateSettingForm().getAttributeName() + "\\E", replaceText);
						//プレースホルダのカラム置き換えでFROM以降のas句は取り除く
						//簡易のSQLを動的queryの対象とする
						queryText = queryText.replaceAll("[\r\n]", "");
						if(replaceText.toLowerCase().contains(" as ")) {
							String replaceSubStringText = replaceText.substring(0,replaceText.toLowerCase().indexOf(" as "));
							String beforeQueryText = queryText.substring(0,queryText.toLowerCase().indexOf(" from "));
							String afterQueryText = queryText.substring(queryText.toLowerCase().indexOf(" from ")+1);
							afterQueryText = afterQueryText.replaceAll("\\Q" + replaceText + "\\E", SQLUtil.sqlEscape(replaceSubStringText));
							queryText = beforeQueryText + " " + afterQueryText;
						}
					}
				}
				resultList = executeQueryDao.executeQuery(queryText);
				graphListForm.setDataList(resultList);
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
			graphListForm.setEditFlag(graphList.getEditFlag());
			graphListForm.setSourceId(graphList.getSourceId());
			List<LayerGraphCooporationForm> layerGraphCooporationFormList = layerGraphCooporationService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setLayerGraphCooporationFormList(layerGraphCooporationFormList);
			if(graphList.getSourceId() != null) {
				LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphList.getSourceId());
				graphListForm.setLayerSourceForm(layerSourceForm);
			}
		}
		return graphListForm;
	}
	
	/**
	 * entity→form詰め替え（query_textの切替項目値を指定のプレースホルダ値で置き換え）
	 * 
	 * @param graphList グラフリストEntity
	 * @param themeId テーマId
	 * @param switchItemMap 切替項目(queryのプレースホルダの置き換えで使用)　{切替項目名：切替項目値}
	 * @return graphListForm グラフリストForm
	 */
	public GraphListForm entityToForm(GraphList graphList,Map<String, String> switchItemMap) {
		GraphListForm graphListForm = new GraphListForm();
		if(graphList != null) {
			graphListForm.setGraphId(graphList.getGraphId());
			graphListForm.setGraphTypeId(graphList.getGraphTypeId());
			graphListForm.setGraphName(graphList.getGraphName());
			graphListForm.setPlaceholderFlag(graphList.getPlaceholderFlag());
			try {
				String queryText = graphList.getQueryText();
				if(queryText != null && queryText.contains("_auto_query_identifier_")) {
					graphListForm.setDeleteFlag("1");
				}
			}catch(Exception e) {}
			List<GraphListTemplateValForm> graphListTemplateValFormList = graphListTemplateValService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setGraphListTemplateValFormList(graphListTemplateValFormList);
			try {
				List<Map<String, Object>> resultList = null;
				String queryText = graphList.getQueryText();
				//切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
				if(switchItemMap != null) {
					Iterator<String> iterator = switchItemMap.keySet().iterator();
					while(iterator.hasNext()) {
						String key = iterator.next();
						String replaceText = switchItemMap.get(key);
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						queryText = queryText.replaceAll("\\Q" + key + "\\E", replaceText);
					}
				}
				//デフォルトの切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
				List<ThemeForm> themeFormList = themeService.findAll();
				for(ThemeForm themeForm:themeFormList) {
					//切替項目でqueryテキストを置き換える( 形式：{placeholeder_name} )
					if(themeForm != null && "1".equals(themeForm.getSwitchFlag()) && queryText != null && themeForm.getSwitchPlaceholderName() != null && themeForm.getSwitchPlaceholderDefaultValue() != null) {
						String replaceText = themeForm.getSwitchPlaceholderDefaultValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						queryText = queryText.replaceAll("\\Q" + themeForm.getSwitchPlaceholderName() + "\\E", replaceText);
					}
				}
				//プレースホルダのカラムを置き換える( 形式：$placeholeder_name$ )
				for(GraphListTemplateValForm graphListTemplateValForm:graphListTemplateValFormList) {
					if(graphListTemplateValForm!=null && graphListTemplateValForm.getItemValue() != null && graphListTemplateValForm.getGraphListTemplateSettingForm() != null && "1".equals(graphListTemplateValForm.getGraphListTemplateSettingForm().getPlaceholderFlag())) {
						String replaceText = graphListTemplateValForm.getItemValue();
						if(replaceText == null) {
							replaceText = "";
						}
						replaceText = SQLUtil.sqlEscape(replaceText);
						queryText = queryText.replaceAll("\\Q" + graphListTemplateValForm.getGraphListTemplateSettingForm().getAttributeName() + "\\E", replaceText);
						//プレースホルダのカラム置き換えでFROM以降のas句は取り除く
						//簡易のSQLを動的queryの対象とする
						queryText = queryText.replaceAll("[\r\n]", "");
						if(replaceText.toLowerCase().contains(" as ")) {
							String replaceSubStringText = replaceText.substring(0,replaceText.indexOf(" as "));
							String beforeQueryText = queryText.substring(0,queryText.toLowerCase().indexOf(" from "));
							String afterQueryText = queryText.substring(queryText.toLowerCase().indexOf(" from ")+1);
							afterQueryText = afterQueryText.replaceAll("\\Q" + replaceText + "\\E", SQLUtil.sqlEscape(replaceSubStringText));
							queryText = beforeQueryText + " " + afterQueryText;
						}
					}
				}
				resultList = executeQueryDao.executeQuery(queryText);
				graphListForm.setDataList(resultList);
			}catch(Exception e) {
				LOGGER.error(e.getMessage());
			}
			graphListForm.setEditFlag(graphList.getEditFlag());
			graphListForm.setSourceId(graphList.getSourceId());
			List<LayerGraphCooporationForm> layerGraphCooporationFormList = layerGraphCooporationService.findByGraphId(graphListForm.getGraphId());
			graphListForm.setLayerGraphCooporationFormList(layerGraphCooporationFormList);
			if(graphList.getSourceId() != null) {
				LayerSourceForm layerSourceForm = layerSourceService.findBySourceId(graphList.getSourceId());
				graphListForm.setLayerSourceForm(layerSourceForm);
			}
		}
		return graphListForm;
	}
	
}
