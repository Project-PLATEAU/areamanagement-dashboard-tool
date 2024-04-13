package view3d.repository.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.PostLayerFeature;
import view3d.form.AttachmentForm;
import view3d.form.PostLayerFeatureForm;
import view3d.service.PostLayerService;

@Component
@Configuration
@ComponentScan
@EnableTransactionManagement
public class PostLayerJdbc {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostLayerService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${app.activity.view.epsg}")
	private Integer viewEpsg;
	
	@Value("${app.activity.data.epsg}")
	private Integer dataEpsg;
	
	@Transactional
	public Integer insert(PostLayerFeatureForm postLayerFeatureForm, String longLat) {
		LOGGER.info("viewEpsg " + viewEpsg + "  dataEpsg " + dataEpsg);
		jdbcTemplate.update("INSERT INTO post_layer_feature (feature_id, layer_id, "
				+ "publish_flag, geometry, post_user_id, post_datetime, parent_feature_id, "
				+ "item_1, item_2, item_3, item_4, item_5, item_6, item_7, item_8, item_9, item_10) "
				+ "VALUES "
				+ "(nextval('post_layer_feature_feature_id_seq'), ?, ?, ST_Transform(ST_GeomFromText('POINT('||?||')', ?), ?), "
				+ "?, CURRENT_TIMESTAMP, ?, "
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
				,
				postLayerFeatureForm.getLayerId(),postLayerFeatureForm.getPublishFlag(),longLat,viewEpsg,dataEpsg,postLayerFeatureForm.getPostUserId(),postLayerFeatureForm.getParentFeatureId(),postLayerFeatureForm.getItem1(),postLayerFeatureForm.getItem2(),postLayerFeatureForm.getItem3(),postLayerFeatureForm.getItem4(),postLayerFeatureForm.getItem5(),postLayerFeatureForm.getItem6(),postLayerFeatureForm.getItem7(),postLayerFeatureForm.getItem8(),postLayerFeatureForm.getItem9(),postLayerFeatureForm.getItem10());
		return jdbcTemplate.queryForObject("SELECT lastval()", Integer.class);
	}
	
	@Transactional
	public Integer quoteInsert(PostLayerFeatureForm postLayerFeatureForm) {
		jdbcTemplate.update("INSERT INTO post_layer_feature " + "(feature_id, layer_id, "
				+ "publish_flag, geometry, post_user_id, post_datetime, parent_feature_id, "
				+ "item_1, item_2, item_3, item_4, item_5, item_6, item_7, item_8, item_9, item_10) "
				+ "VALUES "
				+ "(nextval('post_layer_feature_feature_id_seq'), ?, ?, ST_GeomFromEWKB(ST_AsEWKB(CAST(? AS TEXT))), "
				+ "?, CURRENT_TIMESTAMP, ?, "
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
				,
				postLayerFeatureForm.getLayerId(),postLayerFeatureForm.getPublishFlag(),postLayerFeatureForm.getGeom(),postLayerFeatureForm.getPostUserId(),postLayerFeatureForm.getParentFeatureId(),postLayerFeatureForm.getItem1(),postLayerFeatureForm.getItem2(),postLayerFeatureForm.getItem3(),postLayerFeatureForm.getItem4(),postLayerFeatureForm.getItem5(),postLayerFeatureForm.getItem6(),postLayerFeatureForm.getItem7(),postLayerFeatureForm.getItem8(),postLayerFeatureForm.getItem9(),postLayerFeatureForm.getItem10());
		return jdbcTemplate.queryForObject("SELECT lastval()", Integer.class);
	}
	
	@Transactional
	public String saveNewFile(AttachmentForm attachmentForm) {
		String sql = "UPDATE post_layer_feature SET " + attachmentForm.getItemId() + "=? WHERE feature_id = ?";
		jdbcTemplate.update(sql, attachmentForm.getAttachmentFileName(),attachmentForm.getFeatureId());
		return jdbcTemplate.queryForObject("SELECT " + attachmentForm.getItemId() + " FROM post_layer_feature WHERE feature_id =" + attachmentForm.getFeatureId(), String.class);
	}
}
