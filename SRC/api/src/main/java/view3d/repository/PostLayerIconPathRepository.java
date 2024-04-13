package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.PostLayerIconPath;

@Transactional
@Repository
public interface PostLayerIconPathRepository  extends JpaRepository<PostLayerIconPath, Integer>{
	@Query(value = "SELECT id,layer_id,image_path,judgment_value FROM post_layer_icon_path WHERE layer_id = :layerId AND judgment_value = :judgmentValue ORDER BY id", nativeQuery = true)
	List<PostLayerIconPath> findLayerIdAndJudgmentValue(@Param("layerId") Integer layerId,@Param("judgmentValue") String judgmentValue);
}
