package view3d.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.LoginUser;

@Transactional
@Repository
public interface LoginUserRepository extends JpaRepository<LoginUser, String> {
	/**
	 * ユーザ情報取得
	 * 
	 * @param id       ログインID
	 * @param password パスワード
	 * @return ユーザ情報
	 */
	@Query(value = "SELECT user_id,login_id,password,role FROM login_user WHERE login_id = :loginId AND password = :password ORDER BY user_id ASC", nativeQuery = true)
	List<LoginUser> login(@Param("loginId") String id, @Param("password") String password);
}
