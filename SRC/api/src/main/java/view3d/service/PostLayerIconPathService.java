package view3d.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.PostLayerIconPath;
import view3d.repository.PostLayerIconPathRepository;

@Service
public class PostLayerIconPathService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostLayerIconPathService.class);
	
	@Autowired
	PostLayerIconPathRepository postLayerIconPathRepository;
	
	/**
	 * アイコンパスの取得
	 * @param layerId
	 * @param JudgmentValue
	 * @return List<PostLayerIconPath>
	 */
	public List<PostLayerIconPath> findByLayerIdAndJudgmentValue(Integer layerId,String JudgmentValue ) {
		//LOGGER.info("アイコンパスの取得開始 layerId:" + layerId);
		List<PostLayerIconPath> postLayerIconPathList = postLayerIconPathRepository.findLayerIdAndJudgmentValue(layerId, JudgmentValue);
		//LOGGER.info("アイコンパスの取得終了 layerId:" + layerId);
		return postLayerIconPathList;
	}

}
