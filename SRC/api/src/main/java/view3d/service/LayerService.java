package view3d.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.Layer;
import view3d.entity.PostLayerFeature;
import view3d.form.LayerForm;
import view3d.form.LayerGraphCooporationForm;
import view3d.form.PostLayerFeatureForm;
import view3d.repository.LayerRepository;

@Service
public class LayerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LayerService.class);
	
	@Autowired
	LayerRepository layerRepository;
	
	@Autowired
	LayerGraphCooporationService layerGraphCooporationService;
	
	@Value("${app.billboard.layer.icon.rootpath}")
	 protected String billboardImageRootPath;
	
	/**
	 * 全レイヤの取得
	 * 
	 * @return layerFormList　レイヤFormリスト
	 */
	public List<LayerForm> findAll() {
		LOGGER.info("レイヤ一覧の取得開始");
		List<LayerForm> layerFormList = null;
		List<Layer> layerForm = layerRepository.findAllByOrder();
		if(layerForm != null && layerForm.size() > 0) {
			layerFormList = entityToForm(layerForm);
		}
		LOGGER.info("レイヤ一覧の取得終了");
		return layerFormList;
	}
	
	/**
	 * 投稿・活動タイプの全レイヤを取得
	 * 
	 * @return layerFormList　レイヤFormリスト
	 */
	public List<LayerForm> findAllPostAndActivityLayer() {
		LOGGER.info("レイヤ一覧の取得開始");
		List<LayerForm> layerFormList = null;
		List<Layer> layerForm = layerRepository.findAllPostAndActivityLayer();
		if(layerForm != null && layerForm.size() > 0) {
			layerFormList = entityToForm(layerForm);
		}
		LOGGER.info("レレイヤ一覧の取得終了");
		return layerFormList;
	}
	
	/**
	 * レイヤの取得
	 * 
	 * @param layerId
	 * @return layerForm　レイヤForm
	 */
	public LayerForm findByLayerId(Integer layerId) {
		LOGGER.info("レイヤの取得開始 layerId:" + layerId);
		LayerForm layerForm = null;
		Optional<Layer> layerOpt = layerRepository.findById(layerId);
		if(layerOpt.isPresent()) {
			layerForm = entityToForm(layerOpt.get());
		}
		LOGGER.info("レイヤの取得終了 layerId:" + layerId);
		return layerForm;
	}
	
	/**
	 * レイヤの更新
	 * 
	 * @param LayerForm     投稿レイヤフィーチャエンティティ
	 * @param postLayerFeatureForm 投稿レイヤフィーチャフォーム
	 */
	@Transactional
	public Layer update(Integer layerId, LayerForm layerForm) {
		Optional<Layer> layerOpt = layerRepository.findById(layerId);
		Layer layer = layerOpt.get();
		layer.setLayerId(layerForm.getLayerId());
		layer.setLayerType(layerForm.getLayerType());
		layer.setLayerName(layerForm.getLayerName());
		layer.setIconPath(billboardImageRootPath + layerForm.getIconPath());
		layer.setPlaceHolderFlag(layerForm.getPlaceHolderFlag());
		try {
		    //投稿レイヤのレイヤ設定を取得
		    String layerSettings = layer.getLayerSettings();
		    JSONObject jsonObj = new JSONObject(layerSettings);
		    //レイヤ名セット
		    jsonObj.put("name", layerForm.getLayerName());
		    //凡例の画像パスセット
		    JSONArray legendsObj = jsonObj.getJSONArray("legends");
		    JSONObject legendObj = legendsObj.getJSONObject(0);
		    legendObj.put("url",billboardImageRootPath + layerForm.getIconPath());
		    legendsObj.put(0, legendObj);
		    jsonObj.put("legends", legendsObj);
		    //billboardの画像パスセット
		    JSONObject czmlTemplateObj = jsonObj.getJSONObject("czmlTemplate");
		    JSONObject billboardObj = czmlTemplateObj.getJSONObject("billboard");
		    billboardObj.put("image", billboardImageRootPath + layerForm.getIconPath());
		    czmlTemplateObj.put("billboard", billboardObj);
		    jsonObj.put("czmlTemplate", czmlTemplateObj);
		    //レイヤ設定を更新
		    layer .setLayerSettings(jsonObj.toString());
		}catch(Exception e) {
		    LOGGER.error("レイヤ設定の更新に失敗");
		}
		layerRepository.save(layer);
		LOGGER.info("レイヤ情報を更新 layerId: " + layer.getLayerId());
		Optional<Layer> resLayerOpt = layerRepository.findById(layerId);
		Layer res = resLayerOpt.get();
		return res;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param layer レイヤEntity
	 * @return layerForm レイヤForm
	 */
	public List<LayerForm> entityToForm(List<Layer> layerList) {
		List<LayerForm> layerFormList = new ArrayList<LayerForm>();
		for(Layer layer:layerList) {
			LayerForm layerForm = new LayerForm();
			layerForm.setLayerId(layer.getLayerId());
			layerForm.setIconPath(layer.getIconPath());
			List<LayerGraphCooporationForm> layerGraphCooporationFormList = layerGraphCooporationService.findByLayerId(layerForm.getLayerId());
			layerForm.setLayerGraphCooporationFormList(layerGraphCooporationFormList);
			layerForm.setLayerName(layer.getLayerName());
			layerForm.setLayerSettings(layer.getLayerSettings());
			layerForm.setPlaceHolderFlag(layer.getPlaceHolderFlag());
			layerForm.setLayerType(layer.getLayerType());
			layerFormList.add(layerForm);
		}
		return layerFormList;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param layer レイヤEntity
	 * @return layerForm レイヤForm
	 */
	public LayerForm entityToForm(Layer layer) {
		LayerForm layerForm = new LayerForm();
		if(layerForm != null) {
			layerForm.setLayerId(layer.getLayerId());
			layerForm.setIconPath(layer.getIconPath());
			List<LayerGraphCooporationForm> layerGraphCooporationFormList = layerGraphCooporationService.findByLayerId(layerForm.getLayerId());
			layerForm.setLayerGraphCooporationFormList(layerGraphCooporationFormList);
			layerForm.setLayerName(layer.getLayerName());
			layerForm.setLayerSettings(layer.getLayerSettings());
			layerForm.setPlaceHolderFlag(layer.getPlaceHolderFlag());
			layerForm.setLayerType(layer.getLayerType());
		}
		return layerForm;
	}

}
