package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GraphListTemplateVal;
import view3d.entity.ThemeGraphList;
import view3d.entity.ThemeGraphListPK;

@Transactional
@Repository
public interface ThemeGraphListRepository extends JpaRepository<ThemeGraphList, ThemeGraphListPK> {
	@Query(value = "SELECT theme_id,graph_id,top_left_x,top_left_y,panel_width,panel_height FROM theme_graph_list WHERE theme_id = :themeId ORDER BY theme_id,graph_id ", nativeQuery = true)
	List<ThemeGraphList> findByThemeId(Integer themeId);
	
	@Query(value = "SELECT theme_id,graph_id,top_left_x,top_left_y,panel_width,panel_height FROM theme_graph_list ORDER BY theme_id,graph_id ", nativeQuery = true)
	List<ThemeGraphList> findAll();
	
	@Modifying
	@Query(value="DELETE FROM theme_graph_list WHERE theme_id = :themeId AND graph_id = :graphId",nativeQuery=true)
	int deleteByThemeIdAndGraphId(@Param("themeId") Integer themeId,@Param("graphId") Integer graphId);
	
	@Modifying
	@Query(value="DELETE FROM theme_graph_list WHERE graph_id = :graphId",nativeQuery=true)
	int deleteByGraphId(@Param("graphId") Integer graphId);
}