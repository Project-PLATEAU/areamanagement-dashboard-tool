package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.PostLayerAttribute;
import view3d.entity.PostLayerAttributePK;
import view3d.entity.ThemeLayer;

@Transactional
@Repository
public interface PostLayerAttributeRepository  extends JpaRepository<PostLayerAttribute, PostLayerAttributePK> {
	@Query(value="SELECT layer_id,item_id,item_name,item_type,disp_order,require_flag FROM post_layer_attribute WHERE layer_id = :layerId ORDER BY item_id",nativeQuery=true)
	List<PostLayerAttribute> findByLayerId(@Param("layerId") Integer layerId);
	@Query(value="SELECT layer_id,item_id,item_name,item_type,disp_order,require_flag FROM post_layer_attribute WHERE layer_id = :layerId ORDER BY disp_order",nativeQuery=true)
	List<PostLayerAttribute> findByLayerIdOrderByDispOrder(@Param("layerId") Integer layerId);
	@Query(value="SELECT layer_id,item_id,item_name,item_type,disp_order,require_flag FROM post_layer_attribute WHERE layer_id = :layerId AND item_id = :itemId LIMIT 1",nativeQuery=true)
	PostLayerAttribute findByLayerIdAndItemId(@Param("layerId") Integer layerId,@Param("itemId") Integer itemId);
}
