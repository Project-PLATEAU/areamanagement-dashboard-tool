package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.CitySummary;

@Transactional
@Repository
public interface CitySummaryRepository extends JpaRepository<CitySummary, Integer> {

	/**
	 * region_summaryテーブルを取得
	 * @return
	 */
	@Query(value = "SELECT * FROM region_summary ORDER BY \"カテゴリ\",\"西暦\"", nativeQuery = true)
	List<CitySummary> findCitySummary();

	/**
	 * region_summaryテーブルのIDを検索
	 * @param category
	 * @param jp_ad
	 * @return
	 */
	@Query(value = "SELECT id FROM region_summary WHERE \"カテゴリ\"=:category AND \"和暦\"=:jp_ad", nativeQuery = true)
	Integer getCitySummaryId(String category,String jp_ad);

	/**
	 * region_summaryの最も大きい西暦の値を取得
	 */
	@Query(value = "SELECT Max(\"西暦\") FROM region_summary", nativeQuery = true)
	Integer getMaxYear();
}
