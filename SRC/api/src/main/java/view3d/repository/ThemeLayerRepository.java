package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.ThemeGraphList;
import view3d.entity.ThemeLayer;
import view3d.entity.ThemeLayerPK;

@Transactional
@Repository
public interface ThemeLayerRepository extends JpaRepository<ThemeLayer, ThemeLayerPK>  {
	@Query(value = "SELECT theme_id,layer_id,disp_order,post_flag FROM theme_layer WHERE theme_id = :themeId ORDER BY disp_order ", nativeQuery = true)
	List<ThemeLayer> findByThemeId(Integer themeId);
}
