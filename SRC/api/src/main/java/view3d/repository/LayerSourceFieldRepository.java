package view3d.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.LayerSource;
import view3d.entity.LayerSourceField;

@Transactional
@Repository
public interface LayerSourceFieldRepository extends JpaRepository<LayerSourceField, Integer> {
	
	@Query(value = "SELECT field_id,source_id,field_name,alias FROM layer_source_field WHERE source_id = :sourceId ORDER BY field_id ", nativeQuery = true)
	List<LayerSourceField> findBySourceId(Integer sourceId);
	
	@Query(value = "SELECT field_id,source_id,field_name,alias FROM layer_source_field WHERE source_id = :sourceId AND field_name = :fieldName LIMIT 1 ", nativeQuery = true)
	LayerSourceField findBySourceIdAndFieldName(Integer sourceId,String fieldName);

}
