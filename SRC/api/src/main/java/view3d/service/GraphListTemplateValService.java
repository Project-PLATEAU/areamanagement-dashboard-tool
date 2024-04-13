package view3d.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.GraphList;
import view3d.entity.GraphListTemplateSettingPK;
import view3d.entity.GraphListTemplateVal;
import view3d.form.GraphListTemplateSettingForm;
import view3d.form.GraphListTemplateValForm;
import view3d.repository.GraphListRepository;
import view3d.repository.GraphListTemplateValRepository;

@Service
public class GraphListTemplateValService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphListTemplateValService.class);
	
	@Autowired
	GraphListTemplateValRepository graphListTemplateValRepository;
	
	@Autowired
	GraphListRepository graphListRepository;
	
	@Autowired
	GraphListTemplateSettingService graphListTemplateSettingService;
	
	/**
	 * グラフ・リストテンプレート項目値の取得
	 * 
	 * @param graphListId グラフリストid
	 * @return List<GraphListTemplateValForm> グラフ・リストテンプレート項目値のFormリスト
	 */
	public List<GraphListTemplateValForm> findByGraphId(Integer graphId) {
		LOGGER.info("グラフ・リストテンプレート項目値の取得開始 graphId:" + graphId);
		List<GraphListTemplateValForm> graphListTemplateValFormList = null;
		List<GraphListTemplateVal> graphListTemplateValList = graphListTemplateValRepository.findByGraphId(graphId);
		if(graphListTemplateValList != null && graphListTemplateValList.size() > 0) {
			graphListTemplateValFormList = entityToForm(graphListTemplateValList);
		}
		LOGGER.info("グラフ・リストテンプレート項目値の取得終了 graphId:" + graphId);
		return graphListTemplateValFormList;
	}
	
	/**
	 * entity→form詰め替え(List)
	 * 
	 * @param List<GraphListTemplateVal> グラフ・リストテンプレート項目値のEntityリスト
	 * @return List<GraphListTemplateValForm> グラフ・リストテンプレート項目値のFormリスト
	 */
	public List<GraphListTemplateValForm> entityToForm(List<GraphListTemplateVal> graphListTemplateValList) {
		List<GraphListTemplateValForm> graphListTemplateValFormList = new ArrayList<GraphListTemplateValForm>();
		for(GraphListTemplateVal graphListTemplateVal:graphListTemplateValList) {
			GraphListTemplateValForm graphListTemplateValForm = new GraphListTemplateValForm();
			graphListTemplateValForm.setGraphId(graphListTemplateVal.getId().getGraphId());
			graphListTemplateValForm.setItemId(graphListTemplateVal.getId().getItemId());
			graphListTemplateValForm.setItemValue(graphListTemplateVal.getItemValue());
			GraphListTemplateSettingPK graphListTemplateSettingPK = new GraphListTemplateSettingPK();
			Optional<GraphList> graphListOpt = graphListRepository.findById(graphListTemplateValForm.getGraphId());
			if(graphListOpt.isPresent()) {
				graphListTemplateSettingPK.setGraphTypeId(graphListOpt.get().getGraphTypeId());
			}
			graphListTemplateSettingPK.setItemId(graphListTemplateValForm.getItemId());
			GraphListTemplateSettingForm graphListTemplateSettingForm = graphListTemplateSettingService.findById(graphListTemplateSettingPK);
			graphListTemplateValForm.setGraphListTemplateSettingForm(graphListTemplateSettingForm);
			graphListTemplateValFormList.add(graphListTemplateValForm);
		}
		return graphListTemplateValFormList;
	}
}
