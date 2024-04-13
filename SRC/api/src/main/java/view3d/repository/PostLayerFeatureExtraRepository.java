package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.PostLayerFeature;
import view3d.entity.PostLayerFeatureExtra;

@Transactional
@Repository
public interface PostLayerFeatureExtraRepository extends JpaRepository<PostLayerFeatureExtra, Integer>{
	
	@Query(value="SELECT feature_id,ST_X(ST_TransForm(geometry, 4326)) as longitude,ST_Y(ST_TransForm(geometry, 4326)) as latitude,layer_id,publish_flag,post_user_id,post_datetime,parent_feature_id,item_1,item_2,item_3,item_4,item_5,item_6,item_7,item_8,item_9,item_10 FROM post_layer_feature WHERE layer_id = :layerId ORDER BY post_datetime DESC",nativeQuery=true)
	List<PostLayerFeatureExtra> getByLayerId(@Param("layerId") Integer layerId);
}
