package com.jwk.spring.java.demo.warp;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
	@Bean
	public DataSource createRouterDatasource() {
		AbstractRoutingDataSourceCustom routingDataSource = new MyRoutingDataSource();
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("current:db01",
				createDataSource("jdbc:postgresql://sellpick-dev-main.cluster-c6bwy8w0ihhy.ap-northeast-2.rds.amazonaws.com/sellpickdevSales", "us_sellpick", "us_sellpick"));
		targetDataSources.put("current:db02",
				createDataSource("jdbc:postgresql://sellpick-dev-main.cluster-c6bwy8w0ihhy.ap-northeast-2.rds.amazonaws.com/sellpickdevSales", "us_sellpick", "us_sellpick"));
		routingDataSource.setTargetDataSources(targetDataSources);
		return routingDataSource;
	}

	private DataSource createDataSource(String url, String user, String password) {
		com.zaxxer.hikari.HikariDataSource dataSource = new com.zaxxer.hikari.HikariDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		dataSource.setJdbcUrl(url);
		return dataSource;
	}
}
