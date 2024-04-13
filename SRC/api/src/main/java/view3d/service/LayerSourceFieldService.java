package view3d.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.LayerSourceField;
import view3d.form.LayerSourceFieldForm;
import view3d.form.PostLayerAttributeForm;
import view3d.repository.LayerSourceFieldRepository;
import view3d.util.SQLUtil;

@Service
public class LayerSourceFieldService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LayerSourceFieldService.class);
	
	@Autowired
	LayerSourceFieldRepository layerSourceFieldRepository;
	
	/**
	 * レイヤソースフィールドの取得
	 * 
	 * @param sourceId
	 * @return layerSourceFieldFormList　レイヤソースフィールドFormリスト
	 */
	public List<LayerSourceFieldForm> findBySourceId(Integer sourceId) {
		LOGGER.info("レイヤソースフィールドの取得開始 sourceId:" + sourceId);
		List<LayerSourceFieldForm> layerSourceFieldFormList = null;
		List<LayerSourceField> layerSourceFieldList = layerSourceFieldRepository.findBySourceId(sourceId);
		if(layerSourceFieldList != null && layerSourceFieldList.size() > 0) {
			layerSourceFieldFormList = entityToForm(layerSourceFieldList);
		}
		LOGGER.info("レイヤソースフィールドの取得終了 sourceId:" + sourceId);
		return layerSourceFieldFormList;
	}

	/**
	 * レイヤソースフィールドの更新
	 * 
	 * @param sourceId
	 * @return layerSourceFieldFormList　レイヤソースフィールドFormリスト
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void update(Integer sourceId, List<PostLayerAttributeForm> postLayerAttributeFormList) throws Exception {
		LOGGER.info("レイヤソースフィールドの更新開始 sourceId:" + sourceId);
		List<LayerSourceField> layerSourceFieldList = layerSourceFieldRepository.findBySourceId(sourceId);
		for(PostLayerAttributeForm postLayerAttributeForm: postLayerAttributeFormList) {
			for(LayerSourceField layerSourceField : layerSourceFieldList ){
				String itemName = postLayerAttributeForm.getItemName();
				//エイリアス名のescape処理を行う
				if(itemName != null) {
					itemName = SQLUtil.aliasNameEscape(itemName);
					itemName = SQLUtil.sqlEscape(itemName);
				}
			    if("item_1".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 1){
			        layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_2".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 2){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_3".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 3){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_4".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 4){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_5".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 5){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_6".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 6){
			    	layerSourceField.setAlias(itemName);
			    	layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_7".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 7){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_8".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 8){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_9".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 9){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			    if("item_10".equals(layerSourceField.getFieldName()) && postLayerAttributeForm.getItemId() == 10){
			    	layerSourceField.setAlias(itemName);
			        layerSourceFieldRepository.save(layerSourceField);
			    }
			}
		}
		LOGGER.info("レイヤソースフィールドの更新開始 sourceId:" + sourceId);
	}
	
	/**
	 * entity→form詰め替え(List)
	 * 
	 * @param layerSourceFieldList レイヤソースフィールドリスト
	 * @return layerSourceFieldFormList レイヤソースフィールドFormリスト
	 */
	public List<LayerSourceFieldForm> entityToForm(List<LayerSourceField> layerSourceFieldList) {
		List<LayerSourceFieldForm> layerSourceFieldFormList = new ArrayList<LayerSourceFieldForm>();
		for(LayerSourceField layerSourceField:layerSourceFieldList) {
			LayerSourceFieldForm layerSourceFieldForm = new LayerSourceFieldForm();
			layerSourceFieldForm.setAlias(layerSourceField.getAlias());
			layerSourceFieldForm.setFieldId(layerSourceField.getFieldId());
			layerSourceFieldForm.setFieldName(layerSourceField.getFieldName());
			layerSourceFieldForm.setSourceId(layerSourceField.getSourceId());
			layerSourceFieldFormList.add(layerSourceFieldForm);
		}
		return layerSourceFieldFormList;
	}

}
