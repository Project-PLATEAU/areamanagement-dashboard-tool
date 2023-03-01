package jp.co.ajiko.urbandx.view3d.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed.origins:*}") String[] allowedOrigins) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(convertAllowedOrigins(allowedOrigins));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(false);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * リスト形式へ変換する
	 * 
	 * @param String[] 許可するオリジン一覧
	 * @return List<String> 変換後の許可するオリジン一覧
	 */
	private List<String> convertAllowedOrigins(String[] allowedOrigins) {
		// 値が空の場合はエラーとする
		if (allowedOrigins.length == 0) {
			throw new IllegalArgumentException();
		}
		// 「*」 が設定されている場合は「*」のまま返す
		if (allowedOrigins.length == 1 && allowedOrigins[0].equals("*")) {
			List<String> allowedOriginList = new ArrayList<>();
			allowedOriginList.add("*");
			return allowedOriginList;
		}
		// ホスト名にスキームをつける
		return Arrays.stream(allowedOrigins).collect(Collectors.toList());
	}

}
