package jp.co.ajiko.urbandx.view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiPeople;

@Transactional
@Repository
public interface KaiyuPeopleRepository extends JpaRepository<KaiyuseiPeople, Integer> {

	/**
	 * kaiyu_jinryu_ninzuu_1テーブルを回数⇒日付の順でソートして取得
	 * @return
	 */
	@Query(value = "SELECT * FROM kaiyu_jinryu_ninzuu_1  ORDER BY \"回数\",\"日付\"", nativeQuery = true)
	List<KaiyuseiPeople> findKaiyuPeople();

	@Query(value = "SELECT id FROM kaiyu_jinryu_ninzuu_1 WHERE \"日付\"= cast(:date as date) AND \"回数\"=:number", nativeQuery = true)
	Integer findKaiyuseiPeople_id(String date, Integer number);
}
