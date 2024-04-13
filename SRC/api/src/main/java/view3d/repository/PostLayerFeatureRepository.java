package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.PostLayerFeature;

@Transactional
@Repository
public interface PostLayerFeatureRepository extends JpaRepository<PostLayerFeature, Integer>{

	@Modifying
	@Query(value="SELECT * FROM post_layer_feature WHERE parent_feature_id = :parentFeatureId AND feature_id <> :featureId ORDER BY feature_id",nativeQuery=true)
	List<PostLayerFeature> findByNotCurrentFeatureIdParentFeatureId(@Param("featureId") Integer featureId,@Param("parentFeatureId") Integer parentFeatureId);
	
	@Modifying
	@Query(value="DELETE FROM post_layer_feature WHERE feature_id = :featureId",nativeQuery=true)
	int deleteByFeatureId(@Param("featureId") Integer featureId);

	@Modifying
	@Query(value="SELECT * FROM post_layer_feature WHERE feature_id = :featureId",nativeQuery=true)
	PostLayerFeature getByFeatureId(@Param("featureId") Integer featureId);
	
	@Modifying
	@Query(value="SELECT * FROM post_layer_feature WHERE feature_id = :featureId AND publish_flag ='1'",nativeQuery=true)
	PostLayerFeature getByFeatureIdCanPublish(@Param("featureId") Integer featureId);
}
