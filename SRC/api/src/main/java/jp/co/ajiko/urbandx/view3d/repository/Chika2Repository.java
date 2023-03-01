package jp.co.ajiko.urbandx.view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ajiko.urbandx.view3d.entity.Chika2;

@Transactional
@Repository
public interface Chika2Repository extends JpaRepository<Chika2, Integer> {

	/**
	 * chika2テーブルをエリア⇒区分⇒地点名の順でソートして全件取得
	 * @return
	 */
	@Query(value = "SELECT * FROM chika2 ORDER BY \"エリア\", \"区分\", \"地点名\",\"西暦\"", nativeQuery = true)
	List<Chika2> findChika2();

	/**
	 * chika2テーブルのidを取得
	 * @param placeName
	 * @param category
	 * @param area
	 * @param jp_ad
	 * @return
	 */
	@Query(value = "SELECT id FROM chika2 WHERE \"地点名\"= :placeName AND \"和暦\"= :jp_ad AND \"区分\"=:category AND \"エリア\"=:area", nativeQuery = true)
	Integer findChika2_id(String placeName, String category, String area, String jp_ad);

}
