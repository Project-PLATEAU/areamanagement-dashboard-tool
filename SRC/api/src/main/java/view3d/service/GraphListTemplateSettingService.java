package view3d.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.GraphListTemplateSetting;
import view3d.entity.GraphListTemplateSettingPK;
import view3d.entity.GraphListTemplateVal;
import view3d.form.GraphListTemplateSettingForm;
import view3d.repository.GraphListTemplateSettingRepository;

@Service
public class GraphListTemplateSettingService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphListTemplateSettingService.class);
	
	@Autowired
	GraphListTemplateSettingRepository graphListTemplateSettingRepository;
	
	/**
	 * グラフ・リストテンプレート設定項目の取得
	 * 
	 * @param GraphListTemplateSettingPK item_id,graph_type_id
	 * @return GraphListTemplateSettingForm グラフ・リストテンプレート設定項目Form
	 */
	public GraphListTemplateSettingForm findById(GraphListTemplateSettingPK graphListTemplateSettingPK) {
		LOGGER.info("グラフ・リストテンプレート設定項目の取得開始 item_id:" + graphListTemplateSettingPK.getItemId() + ",graph_type_id:"+graphListTemplateSettingPK.getGraphTypeId());
		GraphListTemplateSettingForm graphListTemplateSettingForm = null;
		Optional<GraphListTemplateSetting> ｇraphListTemplateSettingOpt = graphListTemplateSettingRepository.findById(graphListTemplateSettingPK);
		if(ｇraphListTemplateSettingOpt.isPresent()) {
			graphListTemplateSettingForm = entityToForm(ｇraphListTemplateSettingOpt.get());
		}
		LOGGER.info("グラフ・リストテンプレート設定項目の取得終了 item_id:" + graphListTemplateSettingPK.getItemId() + ",graph_type_id:"+graphListTemplateSettingPK.getGraphTypeId());
		return graphListTemplateSettingForm;
	}
	
	/**
	 * グラフ・リストテンプレート設定項目の取得
	 * 
	 * @param  graphTypeId
	 * @return List<GraphListTemplateSettingForm> グラフ・リストテンプレート設定項目Formリスト
	 */
	public List<GraphListTemplateSettingForm> findByGraphTypeId(Integer graphTypeId) {
		LOGGER.info("グラフ・リストテンプレート設定項目の取得開始 graph_type_id:"+graphTypeId);
		List<GraphListTemplateSettingForm> graphListTemplateSettingFormList = null;
		List<GraphListTemplateSetting> ｇraphListTemplateSettingList = graphListTemplateSettingRepository.findByGraphTypeId(graphTypeId);
		if(ｇraphListTemplateSettingList != null && ｇraphListTemplateSettingList.size() > 0) {
			graphListTemplateSettingFormList = entityToForm(ｇraphListTemplateSettingList);
		}
		LOGGER.info("グラフ・リストテンプレート設定項目の取得終了 graph_type_id:"+graphTypeId);
		return graphListTemplateSettingFormList;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param GraphListTemplateSetting グラフ・リストテンプレート設定項目Entity
	 * @return GraphListTemplateSettingForm グラフ・リストテンプレート設定項目Form
	 */
	public GraphListTemplateSettingForm entityToForm(GraphListTemplateSetting graphListTemplateSetting) {
		GraphListTemplateSettingForm graphListTemplateSettingForm = new GraphListTemplateSettingForm();
		if(graphListTemplateSetting != null) {
			graphListTemplateSettingForm.setItemId(graphListTemplateSetting.getId().getItemId());
			graphListTemplateSettingForm.setGraphTypeId(graphListTemplateSetting.getId().getGraphTypeId());
			graphListTemplateSettingForm.setGroupType(graphListTemplateSetting.getGroupType());
			graphListTemplateSettingForm.setAttributeType(graphListTemplateSetting.getAttributeType());
			graphListTemplateSettingForm.setAttributeName(graphListTemplateSetting.getAttributeName());
			graphListTemplateSettingForm.setDisplayType(graphListTemplateSetting.getDisplayType());
			graphListTemplateSettingForm.setDisplayName(graphListTemplateSetting.getDisplayName());
			graphListTemplateSettingForm.setPlaceholderFlag(graphListTemplateSetting.getPlaceholderFlag());
		}
		return graphListTemplateSettingForm;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param List<GraphListTemplateSetting> グラフ・リストテンプレート設定項目Entityリスト
	 * @return List<GraphListTemplateSettingForm> グラフ・リストテンプレート設定項目Formリスト
	 */
	public List<GraphListTemplateSettingForm> entityToForm(List<GraphListTemplateSetting> graphListTemplateSettingList) {
		List<GraphListTemplateSettingForm> graphListTemplateSettingFormList = new ArrayList<GraphListTemplateSettingForm>();
		for(GraphListTemplateSetting graphListTemplateSetting:graphListTemplateSettingList) {
			GraphListTemplateSettingForm graphListTemplateSettingForm = new GraphListTemplateSettingForm();
			graphListTemplateSettingForm.setItemId(graphListTemplateSetting.getId().getItemId());
			graphListTemplateSettingForm.setGraphTypeId(graphListTemplateSetting.getId().getGraphTypeId());
			graphListTemplateSettingForm.setGroupType(graphListTemplateSetting.getGroupType());
			graphListTemplateSettingForm.setAttributeType(graphListTemplateSetting.getAttributeType());
			graphListTemplateSettingForm.setAttributeName(graphListTemplateSetting.getAttributeName());
			graphListTemplateSettingForm.setDisplayType(graphListTemplateSetting.getDisplayType());
			graphListTemplateSettingForm.setDisplayName(graphListTemplateSetting.getDisplayName());
			graphListTemplateSettingForm.setPlaceholderFlag(graphListTemplateSetting.getPlaceholderFlag());
			graphListTemplateSettingFormList.add(graphListTemplateSettingForm);
		}
		return graphListTemplateSettingFormList;
	}

}
