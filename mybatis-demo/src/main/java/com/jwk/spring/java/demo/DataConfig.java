package com.jwk.spring.java.demo;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class DataConfig {

	@Bean("master")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource master() {
		return DataSourceBuilder.create().build();
	}


	@Bean("dynamicDataSource")
	public DataSource dynamicDataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();
		dataSourceMap.put("master", master());
//		dataSourceMap.put("slave", slave());
		dynamicDataSource.setDefaultDataSource(master());
		dynamicDataSource.setDataSources(dataSourceMap);
		return dynamicDataSource;
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dynamicDataSource) throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dynamicDataSource);
//		sessionFactory.setTypeAliasesPackage("com.louis.**.model"); // Scan the Model
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sessionFactory.setMapperLocations(resolver.getResources("classpath*:sqlmap/*.xml")); // Scan map file
		return sessionFactory;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dynamicDataSource());
	}
}
