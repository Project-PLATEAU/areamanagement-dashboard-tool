package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GraphListTemplateVal;
import view3d.entity.GraphListTemplateValPK;

@Transactional
@Repository
public interface GraphListTemplateValRepository extends JpaRepository<GraphListTemplateVal, GraphListTemplateValPK> {
	
	@Query(value = "SELECT graph_id,item_id,item_value FROM graph_list_template_val WHERE graph_id = :graphId ORDER BY graph_id,item_id ", nativeQuery = true)
	List<GraphListTemplateVal> findByGraphId(Integer graphId);
	
	@Modifying
	@Query(value = "DELETE FROM graph_list_template_val WHERE graph_id = :graphId ", nativeQuery = true)
	int deleteByGraphId(Integer graphId);
}

