package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GraphList;

@Transactional
@Repository
public interface GraphListRepository extends JpaRepository<GraphList, Integer> {
	
	@Query(value = "SELECT graph_id,graph_type_id,graph_name,query_text,edit_flag,source_id,placeholder_flag FROM graph_list ORDER BY graph_id ", nativeQuery = true)
	List<GraphList> findAll();
	
	@Query(value = "SELECT graph_id,graph_type_id,graph_name,query_text,edit_flag,source_id,placeholder_flag FROM graph_list WHERE source_id = :sourceId ORDER BY graph_id ", nativeQuery = true)
	List<GraphList> findBySourceId(Integer sourceId);
	
	@Modifying
	@Query(value = "DELETE FROM graph_list WHERE graph_id = :graphId ", nativeQuery = true)
	int deleteByGraphId(Integer graphId);
}
