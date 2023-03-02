package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.KaiyuseiRegion;

@Transactional
@Repository
public interface KaiyuRegionRepository extends JpaRepository<KaiyuseiRegion, Integer> {

	/**
	 * kaiyu_jinryu_chiiki_1テーブルを回数⇒idの順でソートして取得
	 * @return
	 */
	@Query(value = "SELECT * FROM kaiyu_jinryu_chiiki_1  ORDER BY \"回数\",\"id\"", nativeQuery = true)
	List<KaiyuseiRegion> findKaiyuRegion();

	@Query(value = "SELECT id FROM kaiyu_jinryu_chiiki_1 WHERE \"住所\"= :address AND \"回数\"=:number", nativeQuery = true)
	Integer findKaiyuseiRegion_id(String address, Integer number);
}
