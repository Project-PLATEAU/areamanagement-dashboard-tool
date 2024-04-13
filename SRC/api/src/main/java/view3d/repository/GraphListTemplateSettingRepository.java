package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GraphListTemplateSetting;
import view3d.entity.GraphListTemplateSettingPK;

@Transactional
@Repository
public interface GraphListTemplateSettingRepository extends JpaRepository<GraphListTemplateSetting, GraphListTemplateSettingPK> {
	@Query(value = "SELECT item_id,graph_type_id,attribute_name,attribute_type,display_name,display_type,group_type,placeholder_flag FROM graph_list_template_settings WHERE graph_type_id = :graphTypeId ORDER BY graph_type_id,item_id ", nativeQuery = true)
	List<GraphListTemplateSetting> findByGraphTypeId(Integer graphTypeId);
}

