package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GraphListTemplateVal;
import view3d.entity.GraphListType;

@Transactional
@Repository
public interface GraphListTypeRepository extends JpaRepository<GraphListType, Integer> {
	@Query(value = "SELECT graph_type_id,graph_type_name,edit_flag,default_query_text FROM graph_list_type ORDER BY graph_type_id ", nativeQuery = true)
	List<GraphListType> findAll();
}
