package view3d.dao;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.Layer;
import view3d.entity.PostLayerAttribute;
import view3d.entity.PostLayerFeatureExtra;
import view3d.form.PostSearchForm;

@Transactional
public class PostLayerDao {

	/** Entityマネージャファクトリ */
	protected EntityManagerFactory emf;

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(PostLayerDao.class);
	
	/**
	 * コンストラクタ
	 * 
	 * @param emf Entityマネージャファクトリ
	 */
	public PostLayerDao(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	/**
	 * 投稿レイヤを取得
     * @param themeId テーマID
	 * @return 投稿レイヤ
	 */
	@SuppressWarnings("unchecked")
	public Layer getPostLayerInfo(int themeId) {
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"select tl.layer_id, layer_name, icon_path, layer_type, layer_settings, placeholder_flag from public.theme_layer as tl " + //
					"inner join public.layer as l on tl.layer_id = l.layer_id " + //
					"where l.layer_type = 1 and tl.post_flag = '1' and tl.theme_id = " + themeId;
			Layer result = (Layer) em.createNativeQuery(sql, Layer.class).getSingleResult();
			return result;
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}
	
	/**
	 * 投稿レイヤ属性を取得
     * @param themeId テーマID
	 * @return 投稿レイヤ属性
	 */
	@SuppressWarnings("unchecked")
	public List<PostLayerAttribute> getPostLayerAttribute(int themeId) {
		LOGGER.debug("投稿レイヤ属性取得 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"select pla.layer_id, item_id, item_name, item_type, pla.disp_order, require_flag " + //
					"from public.post_layer_attribute as pla " + //
					"inner join public.theme_layer as tl on tl.layer_id = pla.layer_id " + //
					"inner join public.layer as l on tl.layer_id = l.layer_id " + //
					"where l.layer_type = 1 and tl.post_flag = '1' and tl.theme_id = " + themeId + //
					" order by disp_order";
			return em.createNativeQuery(sql, PostLayerAttribute.class).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("投稿レイヤ属性取得 終了");
		}
	}
	
	/**
	 * 検索データから投稿情報一覧を取得
     * @param layerId　レイヤID
     * @param postSearchForm 投稿検索フォーム
     * @param orderMode ソート順
	 * @return 投稿エンティティ一覧
	 */
	@SuppressWarnings("unchecked")
	public List<PostLayerFeatureExtra> findLayerIdAndSearchData(Integer layerId,PostSearchForm postSearchForm,String orderMode) {
		LOGGER.debug("投稿情報一覧取得 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "SELECT feature_id,ST_X(ST_TransForm(geometry, 4326)) as longitude,ST_Y(ST_TransForm(geometry, 4326)) as latitude,layer_id,publish_flag,post_user_id,post_datetime,parent_feature_id,item_1,item_2,item_3,item_4,item_5,item_6,item_7,item_8,item_9,item_10 "
					+ "FROM post_layer_feature "
					+ "WHERE layer_id = " + layerId + " ";
			//検索開始日時及び終了日時の指定が無い場合は全件検索とする
			if(postSearchForm.getStartPostDateAndTime() != null && !"".equals(postSearchForm.getStartPostDateAndTime().toString()) && postSearchForm.getEndPostDateAndTime() != null && !"".equals(postSearchForm.getEndPostDateAndTime().toString())) {
				sql = sql + " AND post_datetime BETWEEN '" + postSearchForm.getStartPostDateAndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")) + "' AND '" + postSearchForm.getEndPostDateAndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:59")) + "' "
						+ " ORDER BY post_datetime " + orderMode + " ";
			} else {
				sql = sql + " ORDER BY post_datetime " + orderMode + " ";
			}
			return em.createNativeQuery(sql, PostLayerFeatureExtra.class).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("投稿情報一覧取得 終了");
		}
	}
}
