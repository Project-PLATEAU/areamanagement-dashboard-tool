package jp.co.ajiko.urbandx.view3d.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.ajiko.urbandx.view3d.entity.RouteSearch;

@Transactional
public class RouteSearchDao {

	/** Entityマネージャファクトリ */
	protected EntityManagerFactory emf;

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(RouteSearchDao.class);

	/**
	 * コンストラクタ
	 * 
	 * @param emf Entityマネージャファクトリ
	 */
	public RouteSearchDao(EntityManagerFactory emf) {
		this.emf = emf;
	}

	/** エッジ取得SQL: エッジを変えるver */
	private static final String route_search_edge_change = "SELECT id AS seq, cast(link_id AS integer) AS id, cast(start_id AS integer) AS source, cast(end_id AS integer) AS target, $edge_column AS cost FROM link_3d WHERE link_id is not NULL";
	/** エッジ取得SQL: 基本 */
	private static final String route_search_query_base = "SELECT id AS seq, cast(link_id AS integer) AS id, cast(start_id AS integer) AS source, cast(end_id AS integer) AS target, distance AS cost FROM link_3d WHERE link_id is not NULL";

	/** エッジ取得SQL: 車いす条件1 */
	private static final String condition_wheelchair_lv1 = " AND rt_struct=1 AND (route_type=1 OR route_type = 3 OR route_type = 4) AND width = 4 AND vtcl_slope = 1 AND ((rt_struct = 3 AND tfc_signal = 2) OR rt_struct <> 3) AND (elevator = 1 OR elevator = 5)";
	/** エッジ取得SQL: 車いす条件2 */
	private static final String condition_wheelchair_lv2 = " AND (route_type= 1 OR route_type = 3 OR route_type = 4 OR route_type = 7) AND (width = 4 OR width = 3)";
	/** エッジ取得SQL: 車いす条件3 */
	private static final String condition_wheelchair_lv3 = " AND (route_type <> 5 AND route_type <> 6) AND (width = 2 OR width = 3 OR width = 4)";
	/** エッジ取得SQL: 車いす条件4 */
	private static final String condition_wheelchair_lv4 = " AND (route_type <> 5 AND route_type <> 6)";

	/** エッジ取得SQL: 高齢者条件1 */
	private static final String condition_elderly_lv1 = " AND rt_struct = 1 AND (route_type = 1 OR route_type = 4) AND width = 4 AND vtcl_slope = 1 AND lev_diff = 1 AND ((rt_struct = 3 AND tfc_signal = 2) OR rt_struct <> 3) AND (elevator = 1 OR elevator = 5)";
	/** エッジ取得SQL: 高齢者条件2 */
	private static final String condition_elderly_lv2 = " AND (route_type = 1 OR route_type = 4 OR route_type = 5) AND (width = 3 OR width = 4)";
	/** エッジ取得SQL: 高齢者条件3 */
	private static final String condition_elderly_lv3 = " AND (width = 2 OR width = 3 OR width = 4)";
	/** エッジ取得SQL: 高齢者条件4 */
	private static final String condition_elderly_lv4 = "";

	/** エッジ取得SQL: 視覚障害者条件1 */
	private static final String condition_brail_lv1 = " AND rt_struct = 1 AND (route_type = 1 OR route_type = 4) AND width = 4 AND vtcl_slope = 1 AND lev_diff = 1 AND ((rt_struct <> 3 AND rt_struct <> 4) OR (tfc_signal = 2 AND tfc_s_type = 2)) AND brail_tile = 2 AND (elevator = 1 OR elevator = 5)";
	/** エッジ取得SQL: 視覚障害者条件2 */
	private static final String condition_brail_lv2 = " AND route_type <> 3 AND (width = 3 OR width = 4) AND ((rt_struct <> 3 AND rt_struct <> 4) OR ((tfc_signal = 2 OR tfc_signal = 3 OR tfc_signal = 4) AND (tfc_s_type = 2 OR tfc_s_type = 3)))";
	/** エッジ取得SQL: 視覚障害者条件3 */
	private static final String condition_brail_lv3 = " AND route_type <> 3 AND (width = 2 OR width = 3 OR width = 4)";
	/** エッジ取得SQL: 視覚障害者条件4 */
	private static final String condition_brail_lv4 = " AND route_type <> 3";

	/**
	 * エッジつきデータで経路探索
	 * @param startNodeId
	 * @param endNodeId
	 * @param condition
	 * @param resSRID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RouteSearch> searchRouteWithCost(int startNodeId, int endNodeId, int condition, int resSRID) {
		LOGGER.debug("経路探索 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"SELECT ROW_NUMBER() OVER(ORDER BY united.geom ASC) AS result_id, :priority AS priority, " + //
					"ST_AsGeoJSON(ST_Transform(united.geom, :srid)) AS geojson, ST_Length(united.geom) AS distance FROM"
					+ //
					"( " + //
					" SELECT" + //
					" ST_LineMerge(ST_UNION(res.geom)) AS geom " + //
					"FROM" + //
					"( " + //
					"SELECT " + //
					"t1.seq" + //
					", t1" + //
					", edge" + //
					", t2.geom as geom " + //
					"FROM " + //
					"pgr_dijkstra(" + //
					":edge_sql" + //
					", :start_node_id" + //
					", :end_node_id" + //
					", directed \\:= false" + //
					") AS t1 " + //
					"INNER JOIN link_3d AS t2 " + //
					"ON t1.edge = cast(t2.link_id AS integer)" + //
					") as res" + //
					") AS united";
			String edgeColumn = "distance";
			if (condition == 2) {
				edgeColumn = "cost_wheelchair";
			} else if (condition == 3) {
				edgeColumn = "cost_elderly";
			} else if (condition == 4) {
				edgeColumn = "cost_brail";
			}
			String edgeSql = route_search_edge_change.replace("$edge_column", edgeColumn);
			if (condition == 2) {
				edgeSql = edgeSql + condition_wheelchair_lv4;
			} else if (condition == 4) {
				edgeSql = edgeSql + condition_brail_lv4;
			}
			return em.createNativeQuery(sql, RouteSearch.class).setParameter("edge_sql", edgeSql)
					.setParameter("start_node_id", startNodeId).setParameter("end_node_id", endNodeId)
					.setParameter("srid", resSRID).setParameter("priority", 1).getResultList();
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("経路探索 終了");
		}
	}

	/**
	 * 経路探索を実行する
	 * 
	 * @param startNodeId 開始ノードID
	 * @param endNodeId   終了ノードID
	 * @param condition   条件
	 * @param resSRID     レスポンスの座標系
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RouteSearch> searchRoute(int startNodeId, int endNodeId, int condition, int resSRID) {
		LOGGER.debug("経路探索 開始");
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			String sql = "" + //
					"SELECT ROW_NUMBER() OVER(ORDER BY united.geom ASC) AS result_id, :priority AS priority, " + //
					"ST_AsGeoJSON(ST_Transform(united.geom, :srid)) AS geojson, ST_Length(united.geom) AS distance FROM"
					+ //
					"( " + //
					" SELECT" + //
					" ST_LineMerge(ST_UNION(res.geom)) AS geom " + //
					"FROM" + //
					"( " + //
					"SELECT " + //
					"t1.seq" + //
					", t1" + //
					", edge" + //
					", t2.geom as geom " + //
					"FROM " + //
					"pgr_dijkstra(" + //
					":edge_sql" + //
					", :start_node_id" + //
					", :end_node_id" + //
					", directed \\:= false" + //
					") AS t1 " + //
					"INNER JOIN link_3d AS t2 " + //
					"ON t1.edge = cast(t2.link_id AS integer)" + //
					") as res" + //
					") AS united";
			if (condition == 1) {
				// 条件:健常者（条件なしで最短経路を検索）
				LOGGER.debug("検索条件:健常者");
				return em.createNativeQuery(sql, RouteSearch.class).setParameter("edge_sql", route_search_query_base)
						.setParameter("start_node_id", startNodeId).setParameter("end_node_id", endNodeId)
						.setParameter("srid", resSRID).setParameter("priority", 1).getResultList();
			} else {
				String[] q_conditions = new String[4];
				switch (condition) {
				case 2:
					// 条件:車いす
					LOGGER.debug("検索条件:車いす");
					q_conditions[0] = condition_wheelchair_lv1;
					q_conditions[1] = condition_wheelchair_lv2;
					q_conditions[2] = condition_wheelchair_lv3;
					q_conditions[3] = condition_wheelchair_lv4;
					break;
				case 3:
					// 条件:高齢者
					LOGGER.debug("検索条件:高齢者");
					q_conditions[0] = condition_elderly_lv1;
					q_conditions[1] = condition_elderly_lv2;
					q_conditions[2] = condition_elderly_lv3;
					q_conditions[3] = condition_elderly_lv4;
					break;
				case 4:
					// 条件:視覚障害者
					LOGGER.debug("検索条件:視覚障害者");
					q_conditions[0] = condition_brail_lv1;
					q_conditions[1] = condition_brail_lv2;
					q_conditions[2] = condition_brail_lv3;
					q_conditions[3] = condition_brail_lv4;
					break;
				}
				final List<RouteSearch> resList = new ArrayList<RouteSearch>();
				for (int i = 0; i < q_conditions.length; i++) {
					String getEdgeQuery = route_search_query_base + q_conditions[i];
					List<RouteSearch> result = em.createNativeQuery(sql, RouteSearch.class)
							.setParameter("edge_sql", getEdgeQuery).setParameter("start_node_id", startNodeId)
							.setParameter("end_node_id", endNodeId).setParameter("srid", resSRID)
							.setParameter("priority", i + 1).getResultList();
					// 前回検索キャッシュが残るためクリア
					em.clear();
					if ((result.size() > 0 && result.get(0).getGeojson() != null)) {
						// 結果が取得できた場合、結果リストに追加.
						LOGGER.debug("結果を追加:優先度:" + (i + 1));
						resList.add(result.get(0));
					}
				}
				return resList;
			}
		} finally {
			if (em != null) {
				em.close();
			}
			LOGGER.debug("経路探索 終了");
		}
	}
}
