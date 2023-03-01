package jp.co.ajiko.urbandx.view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ajiko.urbandx.view3d.entity.SougouHyoukaResult;

@Transactional
@Repository
public interface SougouHyoukaResultRepository extends JpaRepository<SougouHyoukaResult, Integer> {

	/**
	 * sougou_hyouka_resultから取得
	 * @return
	 */
	@Query(value = "SELECT id FROM sougou_hyouka_result", nativeQuery = true)
	List<SougouHyoukaResult> findSougouHyoukaResult();

	/**
	 * sougou_hyouka_resultテーブルのidを取得
	 * @return
	 */
	@Query(value = "SELECT id FROM sougou_hyouka_result WHERE  \"カテゴリ\"=:category", nativeQuery = true)
	Integer findSHR_id(String category);

	/**
	 * カテゴリと年度を指定して取得
	 */
	@Query(value = "SELECT * FROM sougou_hyouka_result WHERE  \"カテゴリ\"=:category AND \"年度\"=:year", nativeQuery = true)
	List<SougouHyoukaResult> findSHRByCategoryYear(String category,Integer year);

	/**
	 * カテゴリと年度を指定して挿入
	 * @param cityComp
	 * @param pastComp
	 * @param category
	 * @param year
	 */
	@Modifying
	@Query(value = "INSERT INTO sougou_hyouka_result(\"カテゴリ\",\"年度\",\"全体比較\",\"過去比較\") VALUES ( "
			+ ":category,:year,"
			+ ":cityComp, :pastComp )", nativeQuery = true)
	void insertSHRByCategoryAndYear(Double cityComp,Double pastComp, String category,Integer year);

	/**
	 * カテゴリと年度を指定して更新
	 * @param value
	 * @param category
	 */
	@Modifying
	@Query(value = "UPDATE sougou_hyouka_result SET \"全体比較\"=:cityComp, \"過去比較\" =:pastComp WHERE \"カテゴリ\"=:category AND \"年度\"=:year", nativeQuery = true)
	void updateSHRByCategoryAndYear(Double cityComp,Double pastComp, String category,Integer year);

	/**
	 * 年度を指定して総合評価を挿入
	 */
	@Modifying
	@Query(value = "INSERT INTO sougou_hyouka_result(\"カテゴリ\",\"年度\",\"全体比較\",\"過去比較\") VALUES ( "
			+ "'総合評価',:year,"
			+ "(SELECT avg(\"全体比較\") FROM sougou_hyouka_result WHERE \"カテゴリ\" <> '総合評価' AND \"年度\"=:year), "
			+ "(SELECT avg(\"過去比較\") FROM sougou_hyouka_result WHERE \"カテゴリ\" <> '総合評価' AND \"年度\"=:year)"
			+ ")", nativeQuery = true)
	void insertSougouHyoukaByYear(Integer year);

	/**
	 * 年度を指定して総合評価を更新
	 */
	@Modifying
	@Query(value = "UPDATE sougou_hyouka_result SET "
			+ "\"全体比較\"= (SELECT avg(\"全体比較\") FROM sougou_hyouka_result WHERE \"カテゴリ\" <> '総合評価' AND \"年度\"=:year), "
			+ "\"過去比較\" =(SELECT avg(\"過去比較\") FROM sougou_hyouka_result WHERE \"カテゴリ\" <> '総合評価' AND \"年度\"=:year)"
			+ " WHERE \"カテゴリ\"='総合評価' AND \"年度\"=:year", nativeQuery = true)
	void updateSougouHyoukaByYear(Integer year);
}
