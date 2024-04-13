package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.Layer;

@Transactional
@Repository
public interface LayerRepository extends JpaRepository<Layer, Integer> {
	@Query(value = "SELECT layer_id,layer_type,layer_name,layer_settings,icon_path,placeholder_flag FROM layer ORDER BY layer_id", nativeQuery = true)
	public List<Layer> findAllByOrder();
	
	@Query(value = "SELECT layer_id,layer_type,layer_name,layer_settings,icon_path,placeholder_flag FROM layer WHERE layer_type IS NOT NULL AND layer_type > 0 ORDER BY layer_id DESC", nativeQuery = true)
	public List<Layer> findAllPostAndActivityLayer();
}