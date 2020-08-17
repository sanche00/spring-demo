package com.jwk.spring.java.demo;

import com.jwk.spring.java.demo.utils.RestUtils;
import com.jwk.spring.java.demo.utils.RestUtils.PathVar;

import lombok.Data;

@Data
public class TestPath {

	@RestUtils.PathVar
	String id;
	
	@RestUtils.PathVar
	int seq;
}
