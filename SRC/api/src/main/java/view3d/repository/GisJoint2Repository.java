package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GisJoint2;

@Transactional
@Repository
public interface GisJoint2Repository extends JpaRepository<GisJoint2, Integer> {

	/**
	 * gis_joint2テーブルをカテゴリ⇒エリマネ⇒地点名の順でソートしてカテゴリ人口・世帯で取得
	 * @return
	 */
	@Query(value = "SELECT * FROM gis_joint2 as gi INNER JOIN chochomokukai_erimane as ce ON gi.\"地点名\" = ce.s_name WHERE (gi.\"カテゴリ\" ='人口' OR gi.\"カテゴリ\"='世帯')  ORDER BY CASE gi.\"カテゴリ\" WHEN '人口' THEN 1 WHEN '世帯' THEN 2 END,gi.\"エリマネ\",ce.city,ce.s_area,gi.\"西暦\"", nativeQuery = true)
	List<GisJoint2> findGisJoint2ByJinkoSetai();

	/**
	 * gis_joint2テーブルをカテゴリ⇒エリマネ⇒地点名の順でソートしてカテゴリ年齢別で取得
	 * @return
	 */
	@Query(value = "SELECT * FROM gis_joint2  as gi INNER JOIN chochomokukai_erimane as ce ON gi.\"地点名\" = ce.s_name WHERE  (gi.\"カテゴリ\" ='年少人口' OR gi.\"カテゴリ\"='生産年齢人口' OR gi.\"カテゴリ\"='老年人口') ORDER BY CASE gi.\"カテゴリ\" WHEN '年少人口' THEN 1 WHEN '生産年齢人口' THEN 2 WHEN '老年人口' THEN 3 END,gi.\"エリマネ\",ce.city,ce.s_area,gi.\"西暦\"", nativeQuery = true)
	List<GisJoint2> findGisJoint2ByNenreiJinko();

	/**
	 * gis_joint2テーブルをカテゴリ⇒エリマネ⇒地点名の順でソートしてカテゴリ世帯人員別で取得
	 * @return
	 */
	@Query(value = "SELECT * FROM gis_joint2  as gi INNER JOIN chochomokukai_erimane as ce ON gi.\"地点名\" = ce.s_name WHERE (gi.\"カテゴリ\" ='単身世帯' OR gi.\"カテゴリ\"='2人世帯' OR gi.\"カテゴリ\"='3人世帯' OR gi.\"カテゴリ\"='4人世帯' OR gi.\"カテゴリ\"='5人世帯以上') ORDER BY CASE gi.\"カテゴリ\" WHEN '単身世帯' THEN 1 WHEN '2人世帯' THEN 2 WHEN '3人世帯' THEN 3 WHEN '4人世帯' THEN 4 WHEN '5人世帯以上' THEN 5 END,gi.\"エリマネ\",ce.city,ce.s_area,gi.\"西暦\"", nativeQuery = true)
	List<GisJoint2> findGisJoint2BySetaijinin();

	/**
	 * gis_joint2テーブルをカテゴリ⇒エリマネ⇒地点名の順でソートしてカテゴリ事業所で取得
	 * @return
	 */
	@Query(value = "SELECT * FROM gis_joint2  as gi INNER JOIN chochomokukai_erimane as ce ON gi.\"地点名\" = ce.s_name WHERE gi.\"カテゴリ\" ='事業所' ORDER BY gi.\"エリマネ\",ce.city,ce.s_area,gi.\"西暦\"", nativeQuery = true)
	List<GisJoint2> findGisJoint2ByJimusyo();

	/**
	 * gis_joint2テーブルをカテゴリ⇒エリマネ⇒地点名の順でソートしてカテゴリ従業者数で取得
	 * @return
	 */
	@Query(value = "SELECT * FROM gis_joint2  as gi INNER JOIN chochomokukai_erimane as ce ON gi.\"地点名\" = ce.s_name WHERE gi.\"カテゴリ\" ='従業者数' ORDER BY gi.\"エリマネ\",ce.city,ce.s_area,gi.\"西暦\"", nativeQuery = true)
	List<GisJoint2> findGisJoint2ByJugyosya();

	/**
	 * gis_joint2テーブルの任意カテゴリのidを取得
	 * @param area
	 * @param jp_ad
	 * @return
	 */
	@Query(value = "SELECT id FROM gis_joint2 WHERE \"和暦\"= :jp_ad AND \"エリマネ\"=:erimane AND \"地点名\" =:placeName AND \"カテゴリ\" =:category", nativeQuery = true)
	Integer findGisJoint2_id(String placeName, String erimane,String jp_ad, String category);
}
