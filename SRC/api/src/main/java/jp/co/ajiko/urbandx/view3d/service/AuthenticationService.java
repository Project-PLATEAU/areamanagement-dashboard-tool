package jp.co.ajiko.urbandx.view3d.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.ajiko.urbandx.view3d.entity.LoginUser;
import jp.co.ajiko.urbandx.view3d.form.LoginUserForm;
import jp.co.ajiko.urbandx.view3d.repository.LoginUserRepository;
import jp.co.ajiko.urbandx.view3d.util.AuthUtil;


/**
 * 認証系サービスクラス
 */
@Service
@Transactional
public class AuthenticationService {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
	/** M_部署Repositoryインスタンス */
	@Autowired
	protected LoginUserRepository hiroshimaLoginUserRepository;

	/**
	 * ログインユーザ情報取得
	 * 
	 * @param loginId  ログインID
	 * @param password パスワード
	 * @return ユーザ情報
	 */
	public List<LoginUserForm> getLoginUserList(String loginId, String password) {
		LOGGER.debug("ログインユーザ情報取得 開始: " + loginId);
		try {
			List<LoginUserForm> formList = new ArrayList<LoginUserForm>();
			String hash = AuthUtil.createHash(password);
			List<LoginUser> userList = hiroshimaLoginUserRepository.login(loginId, hash);
			for (LoginUser user : userList) {
				formList.add(getLoginUserFormFromEntity(user));
			}
			return formList;
		} finally {
			LOGGER.debug("ログインユーザ情報取得 終了: " + loginId);
		}
	}

	/**
	 * ユーザEntityをユーザフォームに詰めなおす
	 * 
	 * @param ユーザEntity
	 * @return ユーザフォーム
	 */
	private LoginUserForm getLoginUserFormFromEntity(LoginUser entity) {
		LoginUserForm form = new LoginUserForm();
		form.setUserId(entity.getUserId());
		form.setLoginId(entity.getLoginId());
		form.setRole(entity.getRole());
		return form;
	}
}