package view3d.dao;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import view3d.entity.ActivityExtra;
import view3d.form.PostSearchForm;

@Transactional
public class ActivityDao {
	
	/** Entityマネージャファクトリ */
	protected EntityManagerFactory emf;
	
	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityDao.class);
	
	/**
	 * コンストラクタ
	 * 
	 * @param emf Entityマネージャファクトリ
	 */
	public ActivityDao(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	/**
	 * 検索データから活動情報一覧を取得
     * @param activityType　活動タイプ
     * @param postSearchForm 投稿検索フォーム
     * @param orderMode ソート順
	 * @return 活動エンティティ一覧
	 */
	@SuppressWarnings("unchecked")
	public List<ActivityExtra> findActivityTypeAndSearchData(Integer activityType,PostSearchForm postSearchForm,String orderMode) {
		LOGGER.debug("活動情報一覧取得 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "SELECT "
					+ "activity_id,ST_X(ST_TransForm(activity.geom, 4326)) as longitude,ST_Y(ST_TransForm(activity.geom, 4326)) as latitude,insert_time,update_time,start_date_and_time,activity_type,group_type,activity_name,activity_place,activity_content,participants_count,remarks,parent_activity_id,end_date_and_time,post_user_id,publish_flag "
					+ "FROM activity "
					+ "WHERE activity_type = " + activityType + " ";
			//検索開始日時及び終了日時の指定が無い場合は全件検索とする
			if(postSearchForm.getStartPostDateAndTime() != null && !"".equals(postSearchForm.getStartPostDateAndTime().toString()) && postSearchForm.getEndPostDateAndTime() != null && !"".equals(postSearchForm.getEndPostDateAndTime().toString())) {
				sql = sql +" AND insert_time BETWEEN '" + postSearchForm.getStartPostDateAndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")) + "' AND '" + postSearchForm.getEndPostDateAndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:59")) + "' "
					+ " ORDER BY insert_time " + orderMode + " ";
			}else {
				sql = sql + " ORDER BY insert_time " + orderMode + " ";
			}
			return em.createNativeQuery(sql, ActivityExtra.class).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("活動情報一覧取得 終了");
		}
	}
	

}
