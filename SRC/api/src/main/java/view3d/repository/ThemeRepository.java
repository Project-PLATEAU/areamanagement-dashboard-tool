package view3d.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.Theme;

@Transactional
@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
	@Query(value = "SELECT theme_id,theme_name,theme_group_name,disp_order,publish_flag,post_flag,information_text,switch_flag,switch_query,switch_item_name_column_name,switch_item_value_column_name,switch_placeholder_name,switch_placeholder_default_value FROM theme WHERE publish_flag = '1' ORDER BY disp_order,theme_id LIMIT 1 ", nativeQuery = true)
	public Optional<Theme> findDefaultTheme();
	@Query(value = "SELECT theme_id,theme_name,theme_group_name,disp_order,publish_flag,post_flag,information_text,switch_flag,switch_query,switch_item_name_column_name,switch_item_value_column_name,switch_placeholder_name,switch_placeholder_default_value FROM theme ORDER BY disp_order,theme_id ", nativeQuery = true)
	public List<Theme> findAllByOrderBydisp();
	@Query(value = "SELECT theme_id,theme_name,theme_group_name,disp_order,publish_flag,post_flag,information_text,switch_flag,switch_query,switch_item_name_column_name,switch_item_value_column_name,switch_placeholder_name,switch_placeholder_default_value FROM theme WHERE publish_flag = '1' ORDER BY disp_order,theme_id ", nativeQuery = true)
	public List<Theme> findAllByOrderBydispLimited();
}
