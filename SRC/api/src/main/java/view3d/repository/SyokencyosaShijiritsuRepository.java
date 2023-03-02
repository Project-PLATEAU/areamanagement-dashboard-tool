package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.SyokencyosaShijiritsu;

@Transactional
@Repository
public interface SyokencyosaShijiritsuRepository extends JpaRepository<SyokencyosaShijiritsu, Integer> {

	/**
	 * syokencyosa_shijiritsuテーブルを会社名⇒年の順でソートして全件取得
	 * @return
	 */
	@Query(value = "SELECT * FROM syokencyosa_shijiritsu ORDER BY id", nativeQuery = true)
	List<SyokencyosaShijiritsu> findSyokencyosaShijiritsu();

	/**
	 * syokencyosa_shijiritsuテーブルのIDを検索
	 * @param area
	 * @return
	 */
	@Query(value = "SELECT id FROM syokencyosa_shijiritsu WHERE \"商圏エリア\"= :area", nativeQuery = true)
	Integer findSyokencyosaShijiritsu_id(String area);

}
