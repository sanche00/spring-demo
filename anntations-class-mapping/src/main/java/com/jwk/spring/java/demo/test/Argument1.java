package com.jwk.spring.java.demo.test;

import com.jwk.spring.java.demo.ClassMap;

import lombok.Data;

@Data
public class Argument1 {

	@ClassMap(classes = {Result.class}, value = "ret")
	private String data;
}
