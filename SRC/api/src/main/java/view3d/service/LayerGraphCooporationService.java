package view3d.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.LayerGraphCooporation;
import view3d.form.LayerGraphCooporationForm;
import view3d.repository.LayerGraphCooporationRepository;

@Service
public class LayerGraphCooporationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LayerGraphCooporationService.class);
	
	@Autowired
	LayerGraphCooporationRepository layerGraphCooporationRepository;
	
	/**
	 * レイヤ_グラフ連携の取得
	 * 
	 * @param layerId レイヤid
	 * @return List<GraphListTemplateValForm> レイヤ_グラフ連携のFormリスト
	 */
	public List<LayerGraphCooporationForm> findByLayerId(Integer layerId) {
		LOGGER.info("レイヤ_グラフ連携の取得開始 layerId:" + layerId);
		List<LayerGraphCooporationForm> layerGraphCooporationFormList = null;
		List<LayerGraphCooporation> layerGraphCooporationList = layerGraphCooporationRepository.findByLayerId(layerId);
		if(layerGraphCooporationList != null && layerGraphCooporationList.size() > 0) {
			layerGraphCooporationFormList = entityToForm(layerGraphCooporationList);
		}
		LOGGER.info("レイヤ_グラフ連携の取得終了 layerId:" + layerId);
		return layerGraphCooporationFormList;
	}
	
	/**
	 * レイヤ_グラフ連携の取得
	 * 
	 * @param graphListId グラフリストid
	 * @return List<GraphListTemplateValForm> レイヤ_グラフ連携のFormリスト
	 */
	public List<LayerGraphCooporationForm> findByGraphId(Integer graphId) {
		LOGGER.info("レイヤ_グラフ連携の取得開始 graphId:" + graphId);
		List<LayerGraphCooporationForm> layerGraphCooporationFormList = null;
		List<LayerGraphCooporation> layerGraphCooporationList = layerGraphCooporationRepository.findByGraphId(graphId);
		if(layerGraphCooporationList != null && layerGraphCooporationList.size() > 0) {
			layerGraphCooporationFormList = entityToForm(layerGraphCooporationList);
		}
		LOGGER.info("レイヤ_グラフ連携の取得終了 graphId:" + graphId);
		return layerGraphCooporationFormList;
	}
	
	/**
	 * entity→form詰め替え(List)
	 * 
	 * @param List<LayerGraphCooporation> レイヤ_グラフ連携のEntityリスト
	 * @return List<LayerGraphCooporationForm> レイヤ_グラフ連携のFormリスト
	 */
	public List<LayerGraphCooporationForm> entityToForm(List<LayerGraphCooporation> layerGraphCooporationList) {
		List<LayerGraphCooporationForm> layerGraphCooporationFormList = new ArrayList<LayerGraphCooporationForm>();
		for(LayerGraphCooporation layerGraphCooporation:layerGraphCooporationList) {
			LayerGraphCooporationForm layerGraphCooporationForm = new LayerGraphCooporationForm();
			layerGraphCooporationForm.setLayerId(layerGraphCooporation.getLayerId());
			layerGraphCooporationForm.setGraphId(layerGraphCooporation.getGraphId());
			layerGraphCooporationForm.setCooperationId(layerGraphCooporation.getCooperationId());
			layerGraphCooporationForm.setCooperationType(layerGraphCooporation.getCooperationType());
			layerGraphCooporationForm.setCooperationOption(layerGraphCooporation.getCooperationOption());
			layerGraphCooporationFormList.add(layerGraphCooporationForm);
		}
		return layerGraphCooporationFormList;
	}

}
