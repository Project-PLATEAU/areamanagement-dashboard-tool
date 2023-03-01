package jp.co.ajiko.urbandx.view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ajiko.urbandx.view3d.entity.Syogyoshisetsu;

@Transactional
@Repository
public interface SyogyoshisetsuRepository extends JpaRepository<Syogyoshisetsu, Integer> {

	/**
	 * syogyoshisetsuテーブルを開設年の順でソートして全件取得
	 * @return
	 */
	@Query(value = "SELECT * FROM syogyoshisetsu ORDER BY \"開設年\"", nativeQuery = true)
	List<Syogyoshisetsu> findSyogyoshisetsu();

	/**
	 * syogyoshisetsuテーブルのIDを検索
	 * @param storeName
	 * @param address
	 * @param year
	 * @return
	 */
	@Query(value = "SELECT id FROM syogyoshisetsu WHERE \"店舗名\"= :storeName ", nativeQuery = true)
	Integer findSyogyoshisetsu_id(String storeName);

}
