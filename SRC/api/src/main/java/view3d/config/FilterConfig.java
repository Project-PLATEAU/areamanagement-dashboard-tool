package view3d.config;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import view3d.filter.AuthenticationFilter;


/**
 * フィルタ設定定義
 */
@Configuration
public class FilterConfig {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterConfig.class);
	
	/** 管理者のみ許可するパス */
	@Value("${app.filter.admin}")
	private String adminJson;
	/** アクセス不能パス */
	@Value("${app.filter.unable}")
	private String unableJson;

	/**
	 * 認証フィルタをコンポーネントに追加
	 * 
	 * @return 認証フィルタを登録したBean
	 */
	@Bean
	public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
		LOGGER.debug("認証フィルタ定義処理開始");
		// 例外パス定義構築
		List<String> adminList = new ArrayList<String>();
		List<String> unableList = new ArrayList<String>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			adminList = objectMapper.readValue(adminJson, new TypeReference<List<String>>() {
			});
			unableList = objectMapper.readValue(unableJson, new TypeReference<List<String>>() {
			});
		} catch (Exception ex) {
			LOGGER.error("フィルタパス定義初期化エラー", ex);
			LOGGER.error("app.filter.goverment: " + adminJson);
			LOGGER.error("app.filter.unable: " + unableJson);
			
			adminList = new ArrayList<String>();
			unableList = new ArrayList<String>();
		}

		// 認証フィルタのオブジェクトを1番目に実行するフィルタとして追加
		FilterRegistrationBean<AuthenticationFilter> bean = new FilterRegistrationBean<AuthenticationFilter>(
				new AuthenticationFilter(adminList, unableList));
		// コントローラ・静的コンテンツ全てのリクエストに対してフィルタを有効化
		bean.addUrlPatterns("/*");
		// フィルタの実行順序を1に設定
		bean.setOrder(1);

		LOGGER.debug("認証フィルタ定義処理終了");
		return bean;
	}

}

