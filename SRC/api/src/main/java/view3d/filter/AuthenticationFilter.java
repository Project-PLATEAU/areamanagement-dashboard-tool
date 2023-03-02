package view3d.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import view3d.util.AuthUtil;


/**
 * 認証フィルタ
 */
public class AuthenticationFilter implements Filter {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

	/** 例外パス定義 */
	private List<String> ignoreList;

	/** 管理者のみ許可定義 */
	private List<String> adminList;

	/** アクセス不能定義 */
	private List<String> unableList;

	/**
	 * コンストラクタ
	 * 
	 * @param ignoreList 例外パス定義
	 */
	public AuthenticationFilter(List<String> ignoreList, List<String> govermentList, List<String> unableList) {
		this.ignoreList = ignoreList;
		this.adminList = govermentList;
		this.unableList = unableList;
	}

	/**
	 * フィルタ処理
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;

		String requestUri = req.getRequestURI();

		LOGGER.trace("RequestURI: " + requestUri);

		if (isUnable(requestUri)) {
			// 404エラーを返す
			LOGGER.trace("アクセス不可リクエスト: " + requestUri);
			HttpServletResponse res = (HttpServletResponse) response;
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// 認証結果
		boolean flag = false;
		if (isIgnore(requestUri)) {
			// 例外パス
			LOGGER.trace("認証不要リクエスト: " + requestUri);
			flag = true;
		} else {
			// それ以外はトークンチェック
			Cookie[] cookies = req.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie cookie = cookies[i];
					if (cookie.getName().equals(AuthUtil.TOKEN)) {
						String role = AuthUtil.getRole(cookie.getValue());
						if (role != null) {
							// 管理者のみ許可されたパスではないか確認
							if (isAdminOnly(requestUri)) {
								LOGGER.trace("管理者のみアクセス可能リクエスト: " + requestUri);
								if (AuthUtil.ROLE_ADMIN.equals(role)) {
									LOGGER.trace("ユーザ種別: 管理者");
									flag = true;
								}
							} else {
								// ユーザもアクセス可
								flag = true;
							}
						}
						break;
					}
				}
			}
		}
		if (flag) {
			LOGGER.trace("アクセス可能");
			chain.doFilter(request, response);
		} else {
			LOGGER.trace("アクセス不可");
			HttpServletResponse res = (HttpServletResponse) response;
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	/**
	 * 認証フリーなパスかどうか
	 * 
	 * @param uri URI
	 * @return 判定結果
	 */
	private boolean isIgnore(String uri) {
		for (String ignore : ignoreList) {
			if (ignore == null || "".equals(ignore)) {
				// 空文字、NULLは無効
				continue;
			}

			if (uri.startsWith(ignore)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 管理者のみアクセス可能なパスか
	 * 
	 * @param uri URI
	 * @return 判定結果
	 */
	private boolean isAdminOnly(String uri) {
		// govermentList
		for (String admin : adminList) {
			if (admin == null || "".equals(admin)) {
				// 空文字、NULLは無効
				continue;
			}

			if (uri.startsWith(admin)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * アクセス不能なパスか
	 * 
	 * @param uri URI
	 * @return 判定結果
	 */
	private boolean isUnable(String uri) {
		// govermentList
		for (String unable : unableList) {
			if (unable == null || "".equals(unable)) {
				// 空文字、NULLは無効
				continue;
			}

			if (uri.startsWith(unable)) {
				return true;
			}
		}
		return false;
	}
}
