package jp.co.ajiko.urbandx.view3d.entity;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.co.ajiko.urbandx.view3d.dao.KaiyuseiDao;

@Configuration
public class DBConnect implements Serializable {
	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Bean
	KaiyuseiDao kaiyuseiDao() {
		return new KaiyuseiDao(url,username,password);
	}

}
