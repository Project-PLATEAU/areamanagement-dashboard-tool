package view3d.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.LayerSource;
import view3d.entity.LayerSourceField;
import view3d.form.LayerForm;
import view3d.form.LayerSourceFieldForm;
import view3d.form.LayerSourceForm;
import view3d.repository.LayerSourceFieldRepository;
import view3d.repository.LayerSourceRepository;

@Service
public class LayerSourceService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LayerSourceService.class);
	
	@Autowired
	LayerSourceRepository layerSourceRepository;
	
	@Autowired
	LayerSourceFieldService layerSourceFieldService;
	
	@Autowired
	LayerService layerService;
	
	/**
	 * レイヤソースの取得
	 * 
	 * @param layerId
	 * @return layerForm　レイヤForm
	 */
	public List<LayerSourceForm> findAll() {
		LOGGER.info("レイヤソースの取得開始");
		List<LayerSourceForm> layerSourceFormList = null;
		List<LayerSource> layerSourceList = layerSourceRepository.findAll();
		if(layerSourceList != null && layerSourceList.size() > 0) {
			layerSourceFormList = entityToForm(layerSourceList);
		}
		LOGGER.info("レイヤソースの取得終了");
		return layerSourceFormList;
	}
	
	/**
	 * レイヤソースの取得
	 * 
	 * @param layerId
	 * @return layerForm　レイヤForm
	 */
	public LayerSourceForm findBySourceId(Integer sourceId) {
		LOGGER.info("レイヤソースの取得開始 sourceId:" + sourceId);
		LayerSourceForm layerSourceForm = null;
		Optional<LayerSource> layerSourceOpt = layerSourceRepository.findById(sourceId);
		if(layerSourceOpt.isPresent()) {
			layerSourceForm = entityToForm(layerSourceOpt.get());
		}
		LOGGER.info("レイヤソースの取得終了 sourceId:" + sourceId);
		return layerSourceForm;
	}
	
	/**
	 * レイヤソースの取得
	 * 
	 * @param layerId
	 * @return layerForm　レイヤForm
	 */
	public LayerSourceForm findByLayerId(Integer layerId) {
		LOGGER.info("レイヤソースの取得開始 layerId:" + layerId);
		LayerSourceForm layerSourceForm = null;
		Optional<LayerSource> layerSourceOpt = layerSourceRepository.findByLayerId(layerId);
		if(layerSourceOpt.isPresent()) {
			layerSourceForm = entityToForm(layerSourceOpt.get());
		}
		LOGGER.info("レイヤソースの取得終了 layerId:" + layerId);
		return layerSourceForm;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param LayerSource レイヤソースEntity
	 * @return layerSourceForm レイヤソースForm
	 */
	public LayerSourceForm entityToForm(LayerSource layerSource) {
		LayerSourceForm layerSourceForm = new LayerSourceForm();
		if(layerSource != null) {
			layerSourceForm.setLayerId(layerSource.getLayerId());
			layerSourceForm.setSourceId(layerSource.getSourceId());
			layerSourceForm.setTableName(layerSource.getTableName());
			List<LayerSourceFieldForm> layerSourceFieldFormList = layerSourceFieldService.findBySourceId(layerSource.getSourceId());
			layerSourceForm.setLayerSourceFieldFormList(layerSourceFieldFormList);
		}
		return layerSourceForm;
	}
	
	/**
	 * entity→form詰め替え
	 * 
	 * @param layerSourceList レイヤソースEntity
	 * @return layerSourceFormList レイヤソースForm
	 */
	public List<LayerSourceForm> entityToForm(List<LayerSource> layerSourceList) {
		List<LayerSourceForm> layerSourceFormList = new ArrayList<LayerSourceForm>();
		if(layerSourceList != null) {
			for(LayerSource layerSource:layerSourceList) {
				LayerSourceForm layerSourceForm = new LayerSourceForm();
				layerSourceForm.setLayerId(layerSource.getLayerId());
				layerSourceForm.setSourceId(layerSource.getSourceId());
				layerSourceForm.setTableName(layerSource.getTableName());
				List<LayerSourceFieldForm> layerSourceFieldFormList = layerSourceFieldService.findBySourceId(layerSource.getSourceId());
				layerSourceForm.setLayerSourceFieldFormList(layerSourceFieldFormList);
				LayerForm layerForm = layerService.findByLayerId(layerSource.getLayerId());
				layerSourceForm.setLayerForm(layerForm);
				layerSourceFormList.add(layerSourceForm);
			}
		}
		return layerSourceFormList;
	}

}
