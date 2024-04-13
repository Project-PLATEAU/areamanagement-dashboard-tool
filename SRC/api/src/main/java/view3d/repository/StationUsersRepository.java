package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.StationUsers;

@Transactional
@Repository
public interface StationUsersRepository extends JpaRepository<StationUsers, Integer> {

	/**
	 * station_usersテーブルを会社名⇒年の順でソートして全件取得
	 * @return
	 */
	@Query(value = "SELECT * FROM station_users ORDER BY \"会社名\",\"年\"", nativeQuery = true)
	List<StationUsers> findStationUsers();

	@Query(value = "SELECT id FROM station_users WHERE \"会社名\"= :officeName AND \"年\"=:year", nativeQuery = true)
	Integer findStationUsers_id(String officeName, String year);
}
