package com.jwk.spring.java.demo.warp;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/route")
@Slf4j
public class TestController {
	
	@Autowired
	DataSource createRouterDatasource;
	@Autowired
	private MyMapper myMapper;
	@GetMapping("/a")
	public String getA(HttpSession session) {
		session.setAttribute("db_key", "db01");
		return myMapper.findDbName();
	}

	@GetMapping("/b")
	public String getB(HttpSession session) {
		session.setAttribute("db_key", "db02");
		return myMapper.findDbName();
	}
	
	@GetMapping("/add/{key}")
	public void add(@PathVariable("key") String key) {
		log.info("{}", createRouterDatasource);
		MyRoutingDataSource myRoutingDataSource = MyRoutingDataSource.class.cast(createRouterDatasource);
		myRoutingDataSource.addDatasurce(key, createDataSource("jdbc:postgresql://sellpick-dev-main.cluster-c6bwy8w0ihhy.ap-northeast-2.rds.amazonaws.com/sellpickdevSales", "us_sellpick", "us_sellpick"));
	}
	@GetMapping("/get/{key}")
	public String get(HttpSession session, @PathVariable("key") String key) {
		session.setAttribute("db_key", key);
		return myMapper.findDbName();
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
