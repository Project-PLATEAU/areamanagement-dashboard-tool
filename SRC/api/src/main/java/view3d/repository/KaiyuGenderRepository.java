package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.KaiyuseiGender;

@Transactional
@Repository
public interface KaiyuGenderRepository extends JpaRepository<KaiyuseiGender, Integer> {

	/**
	 * kaiyu_jinryu_seibetsu_1テーブルを回数⇒idの順でソートして取得
	 * @return
	 */
	@Query(value = "SELECT * FROM kaiyu_jinryu_seibetsu_1  ORDER BY \"回数\",\"id\"", nativeQuery = true)
	List<KaiyuseiGender> findKaiyuGender();

	@Query(value = "SELECT id FROM kaiyu_jinryu_seibetsu_1 WHERE \"性別\"= :gender AND \"回数\"=:number", nativeQuery = true)
	Integer findKaiyuseiGender_id(String gender, Integer number);
}
