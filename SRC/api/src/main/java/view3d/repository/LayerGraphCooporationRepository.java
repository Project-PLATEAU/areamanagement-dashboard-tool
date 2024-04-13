package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GraphListTemplateVal;
import view3d.entity.LayerGraphCooporation;

@Transactional
@Repository
public interface LayerGraphCooporationRepository extends JpaRepository<LayerGraphCooporation, Integer> {
	
	@Query(value = "SELECT cooperation_id,cooperation_type,cooperation_option,layer_id,graph_id FROM layer_graph_cooporation WHERE graph_id = :graphId ORDER BY cooperation_id ", nativeQuery = true)
	List<LayerGraphCooporation> findByGraphId(Integer graphId);
	
	@Query(value = "SELECT cooperation_id,cooperation_type,cooperation_option,layer_id,graph_id FROM layer_graph_cooporation WHERE layer_id = :layerId ORDER BY cooperation_id ", nativeQuery = true)
	List<LayerGraphCooporation> findByLayerId(Integer layerId);
	
	@Modifying
	@Query(value = "DELETE FROM layer_graph_cooporation WHERE graph_id = :graphId ", nativeQuery = true)
	int deleteByGraphId(Integer graphId);
}
