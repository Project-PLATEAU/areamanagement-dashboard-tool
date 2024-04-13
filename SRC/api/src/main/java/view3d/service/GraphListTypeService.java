package view3d.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.GraphListType;
import view3d.form.GraphListTemplateSettingForm;
import view3d.form.GraphListTypeForm;
import view3d.repository.GraphListTypeRepository;

@Service
public class GraphListTypeService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphListTypeService.class);
	
	@Autowired
	GraphListTypeRepository graphListTypeRepository;
	
	@Autowired
	GraphListTemplateSettingService graphListTemplateSettingService;
	
	/**
	 * グラフ・リスト タイプ情報の取得
	 * 
	 * @return List<GraphListTypeForm> グラフリストタイプFormリスト
	 */
	public List<GraphListTypeForm> findAll() {
		LOGGER.info("グラフ・リストタイプ情報の取得開始");
		List<GraphListTypeForm> graphListTypeFormList = null;
		List<GraphListType> graphListTypeList = graphListTypeRepository.findAll();
		if(graphListTypeList != null && graphListTypeList.size() > 0) {
			graphListTypeFormList = entityToForm(graphListTypeList);
		}
		LOGGER.info("グラフ・リストタイプ情報の取得終了");
		return graphListTypeFormList;
	}
	
	/**
	 * entity→form詰め替え(List)
	 * 
	 * @param List<GraphListType> グラフリストタイプのEntityリスト
	 * @return List<GraphListTypeForm> グラフリストタイプのFormリスト
	 */
	public List<GraphListTypeForm> entityToForm(List<GraphListType> graphListTypeList) {
		List<GraphListTypeForm> graphListTypeFormList = new ArrayList<GraphListTypeForm>();
		for(GraphListType graphListType:graphListTypeList) {
			GraphListTypeForm graphListTypeForm = new GraphListTypeForm();
			graphListTypeForm.setEditFlag(graphListType.getEditFlag());
			List<GraphListTemplateSettingForm> graphListTemplateSettingFormList = graphListTemplateSettingService.findByGraphTypeId(graphListType.getGraphTypeId());
			graphListTypeForm.setGraphListTemplateSettingFormList(graphListTemplateSettingFormList);
			graphListTypeForm.setGraphTypeId(graphListType.getGraphTypeId());
			graphListTypeForm.setGraphTypeName(graphListType.getGraphTypeName());
			graphListTypeFormList.add(graphListTypeForm);
		}
		return graphListTypeFormList;
	}

}
