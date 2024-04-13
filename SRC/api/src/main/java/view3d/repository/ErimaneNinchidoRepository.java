package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.ErimaneNinchido;

@Transactional
@Repository
public interface ErimaneNinchidoRepository extends JpaRepository<ErimaneNinchido, Integer> {

	/**
	 * erimane_ninchidoテーブルをエリアの順でソートして全件取得
	 * @return
	 */
	@Query(value = "SELECT * FROM erimane_ninchido ORDER BY \"エリア\",\"西暦\"", nativeQuery = true)
	List<ErimaneNinchido> findErimaneNinchido();

	/**
	 * erimane_ninchidoテーブルのidを取得
	 * @param area
	 * @param jp_ad
	 * @return
	 */
	@Query(value = "SELECT id FROM erimane_ninchido WHERE \"和暦\"= :jp_ad AND \"エリア\"=:area", nativeQuery = true)
	Integer findNinchido_id(String area, String jp_ad);
}
