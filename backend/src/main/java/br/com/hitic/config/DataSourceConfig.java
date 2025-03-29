package br.com.hitic.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Bean
	DataSource dataSource() {
		return DataSourceBuilder.create().url(url).username(username).password(password)
				.driverClassName(driverClassName).build();
	}

	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory dataSource) {
		return new JpaTransactionManager(dataSource);
	}
}
