package view3d.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.LoginUser;
import view3d.form.UserForm;
import view3d.repository.LoginUserRepository;
import view3d.util.AuthUtil;


/**
 * ユーザ情報サービスクラス
 */
@Service
@Transactional
public class UserService {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	
	/** Repositoryインスタンス */
	@Autowired
	protected LoginUserRepository loginUserRepository;

	/**
	 * ログインユーザ情報取得
	 * 
	 * @param loginId  ログインID
	 * @param password パスワード
	 * @return ユーザ情報
	 */
	public List<UserForm> getLoginUserFormList(String loginId, String password) {
		LOGGER.debug("ログインユーザ情報取得 開始: " + loginId);
		try {
			List<UserForm> formList = new ArrayList<UserForm>();
			String hash = AuthUtil.createHash(password);
			List<LoginUser> userList = loginUserRepository.login(loginId, hash);
			for (LoginUser user : userList) {
				formList.add(getUserFormFromEntity(user));
			}
			return formList;
		} finally {
			LOGGER.debug("ログインユーザ情報取得 終了: " + loginId);
		}
	}
	
	/**
	 * ユーザ情報取得
	 * @param userId
	 * @return ユーザ情報
	 */
	public UserForm getUserFormByUserId(Integer userId) {
		LOGGER.debug("ユーザ情報取得 開始: " + userId);
		try {
			UserForm userForm = null;
			Optional<LoginUser> userOpt = loginUserRepository.findById(userId);
			if(userOpt.isPresent()) {
				userForm = getUserFormFromEntity(userOpt.get());
			}
			return userForm;
		} finally {
			LOGGER.debug("ユーザ情報取得 終了: " + userId);
		}
	}
	
	/**
	 * 全てのユーザ情報取得
	 * 
	 * @return ユーザ情報
	 */
	public List<UserForm> getAllUserFormList() {
		LOGGER.debug("全てのユーザ情報取得 開始 ");
		try {
			List<UserForm> formList = new ArrayList<UserForm>();
			List<LoginUser> userList = loginUserRepository.getAll();
			for (LoginUser user : userList) {
				formList.add(getUserFormFromEntity(user));
			}
			return formList;
		} finally {
			LOGGER.debug("全てのユーザ情報取得 終了 ");
		}
	}
	
	/**
	 * ユーザー情報を新規作成
	 * @param userForm  ユーザ情報
	 * @return boolean true:成功 false:失敗
	 */
	public boolean register(UserForm userForm) {
		LOGGER.debug("ユーザー情報を新規作成 開始 ");
		boolean result = false;
		try {
			if(userForm.getUserId() == null) {
				LoginUser entity = getEntityFromUserForm(userForm);
				loginUserRepository.save(entity);
				result = true;
			}
		} finally {
			LOGGER.debug("ユーザー情報を新規作成 終了 ");
		}
		return result;
	}
	
	/**
	 * ユーザー情報を更新
	 * @param userForm  ユーザ情報
	 * @return boolean true:成功 false:失敗
	 */
	public boolean update(UserForm userForm) {
		LOGGER.debug("ユーザー情報を更新 開始 ");
		boolean result = false;
		try {
			Optional<LoginUser> userOpt = loginUserRepository.findById(userForm.getUserId());
			if(userOpt.isPresent()) {
				LoginUser entity = getEntityFromUserForm(userForm);
				loginUserRepository.save(entity);
				result = true;
			}
		} finally {
			LOGGER.debug("ユーザー情報を更新 終了 ");
		}
		return result;
	}
	
	/**
	 * ユーザー情報を削除
	 * @param userForm  ユーザ情報
	 * @return boolean true:成功 false:失敗
	 */
	public boolean delete(Integer userId) {
		LOGGER.debug("ユーザー情報を削除 開始 ");
		boolean result = false;
		try {
			Optional<LoginUser> userOpt = loginUserRepository.findById(userId);
			if(userOpt.isPresent()) {
				loginUserRepository.deleteById(userId);
				result = true;
			}
		} finally {
			LOGGER.debug("ユーザー情報を削除 終了 ");
		}
		return result;
	}

	/**
	 * ユーザEntityをユーザフォームに詰めなおす
	 * 
	 * @param ユーザEntity
	 * @return ユーザフォーム
	 */
	private UserForm getUserFormFromEntity(LoginUser entity) {
		UserForm form = new UserForm();
		form.setUserId(entity.getUserId());
		form.setLoginId(entity.getLoginId());
		form.setRole(entity.getRole());
		form.setUserName(entity.getUserName());
		form.setMailAddress(entity.getMailAddress());
		return form;
	}
	
	/**
	 * ユーザフォームをユーザEntityに詰めなおす
	 * 
	 * @param ユーザEntity
	 * @return ユーザフォーム
	 */
	private LoginUser getEntityFromUserForm(UserForm form) {
		LoginUser entity = new LoginUser();
		entity.setUserId(form.getUserId());
		String hash = AuthUtil.createHash(form.getPassword());
		entity.setPassword(hash);
		entity.setLoginId(form.getLoginId());
		entity.setRole(form.getRole());
		entity.setUserName(form.getUserName());
		entity.setMailAddress(form.getMailAddress());
		return entity;
	}
}