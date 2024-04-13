package view3d.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.LayerSource;

@Transactional
@Repository
public interface LayerSourceRepository extends JpaRepository<LayerSource, Integer> {

	@Query(value = "SELECT source_id,layer_id,table_name FROM layer_source WHERE layer_id = :layerId", nativeQuery = true)
	Optional<LayerSource> findByLayerId(Integer layerId);

}
