package view3d.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import view3d.entity.ChochomokukaiErimane;

@Transactional
public class ChochomokukaiErimaneDao{
	
	/** Entityマネージャファクトリ */
	protected EntityManagerFactory emf;

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChochomokukaiErimaneDao.class);

	/**
	 * コンストラクタ
	 * 
	 * @param emf Entityマネージャファクトリ
	 */
	public ChochomokukaiErimaneDao(EntityManagerFactory emf) {
		this.emf = emf;
	}
	
	/**
	 * エリマネ町丁目名から町丁目一覧を取得
     * @param epsg  緯度経度座標系EPSG
	 * @return 町丁目一覧
	 */
	@SuppressWarnings("unchecked")
	public List<ChochomokukaiErimane> getDistrictList(int epsg) {
		LOGGER.debug("町丁目一覧取得 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"SELECT id , key_code , pref , city , s_area , pref_name , city_name , s_name , area_management_type , " + //
                    "  ST_X(ST_Centroid(ST_Envelope(ST_Transform(geom, " + epsg + ")))) AS lon, " + //
                    "  ST_Y(ST_Centroid(ST_Envelope(ST_Transform(geom, " + epsg + ")))) AS lat, " + //
                    "  ST_XMin(ST_Transform(geom," + epsg + ")) AS min_lon, " + //
                    "  ST_YMin(ST_Transform(geom," + epsg + ")) AS min_lat, " + //
                    "  ST_XMax(ST_Transform(geom," + epsg + ")) AS max_lon, " + //
                    "  ST_YMax(ST_Transform(geom," + epsg + ")) AS max_lat " + //
					"  FROM chochomokukai_erimane ORDER BY id";
			return em.createNativeQuery(sql, ChochomokukaiErimane.class).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("町丁目一覧取得 終了");
		}
	}
	
	/**
	 * エリマネ町丁目名から町丁目を検索
     * @param epsg  緯度経度座標系EPSG
	 * @return 町丁目一覧
	 */
	@SuppressWarnings("unchecked")
	public List<ChochomokukaiErimane> searchChochomoku(int epsg, String town, String block) {
		LOGGER.debug("町丁目検索 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String param = "";
			if(!town.equals("町名") || !block.equals("丁目名")) {
				param += "WHERE ";
				String townParam = "";
				String blockParam = "";
				if(!town.equals("町名")) townParam = "town_name='" + town +"'";
				if(!block.equals("丁目名")) blockParam = " AND block_name='" + block + "' ";
				param += townParam + blockParam;
			}
			String sql = "" + //
					"SELECT id , key_code , pref , city , s_area , pref_name , city_name , s_name , area_management_type , " + //
                    "  ST_X(ST_Centroid(ST_Envelope(ST_Transform(geom, " + epsg + ")))) AS lon, " + //
                    "  ST_Y(ST_Centroid(ST_Envelope(ST_Transform(geom, " + epsg + ")))) AS lat, " + //
                    "  ST_XMin(ST_Transform(geom," + epsg + ")) AS min_lon, " + //
                    "  ST_YMin(ST_Transform(geom," + epsg + ")) AS min_lat, " + //
                    "  ST_XMax(ST_Transform(geom," + epsg + ")) AS max_lon, " + //
                    "  ST_YMax(ST_Transform(geom," + epsg + ")) AS max_lat " + //
					"  FROM chochomokukai_erimane " + param + "ORDER BY id";
			//LOGGER.debug("sql : " + sql);
			return em.createNativeQuery(sql, ChochomokukaiErimane.class).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("町丁目検索 終了");
		}
	}

	/**
	 * エリマネ町丁目名から町丁一覧を取得
	 * @return 町一覧
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTownList() {
		LOGGER.debug("町名一覧取得 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"SELECT DISTINCT town_name FROM chochomokukai_erimane WHERE town_name IS NOT NULL";
			return em.createNativeQuery(sql).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("町名一覧取得 終了");
		}
	}
	
	/**
	 * エリマネ町丁目名から町丁一覧を取得
	 * @return 町一覧
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getChochoList() {
		LOGGER.debug("町丁一覧取得 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"SELECT DISTINCT id, town_name, block_name FROM chochomokukai_erimane ORDER BY id";
			return em.createNativeQuery(sql).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("町丁一覧取得 終了");
		}
	}
}
