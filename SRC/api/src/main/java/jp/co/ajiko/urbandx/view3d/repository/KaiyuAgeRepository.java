package jp.co.ajiko.urbandx.view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiAge;

@Transactional
@Repository
public interface KaiyuAgeRepository extends JpaRepository<KaiyuseiAge, Integer> {

	/**
	 * kaiyu_jinryu_nenrei_1テーブルを回数⇒項目idの順でソートして取得
	 * @return
	 */
	@Query(value = "SELECT * FROM kaiyu_jinryu_nenrei_1  ORDER BY \"回数\",\"項目id\"", nativeQuery = true)
	List<KaiyuseiAge> findKaiyuAge();

	/**
	 * kaiyu_jinryu_nenrei_1テーブルで項目と回数を指定して項目idを取得
	 * @param item
	 * @param number
	 * @return
	 */
	@Query(value = "SELECT id FROM kaiyu_jinryu_nenrei_1 WHERE \"項目\"= :item AND \"回数\"=:number", nativeQuery = true)
	Integer findKaiyuseiAge_id(String item, Integer number);

	@Query(value = "SELECT \"項目id\" FROM kaiyu_jinryu_nenrei_1 WHERE \"項目\"= :item", nativeQuery = true)
	Integer findKaiyuseiAgeItemId(String item);
}
