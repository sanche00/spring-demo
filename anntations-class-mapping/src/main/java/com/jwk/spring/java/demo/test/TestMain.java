package com.jwk.spring.java.demo.test;

import com.jwk.spring.java.demo.ClassMapping;

public class TestMain {

	public static void main(String[] args) {
		Argument1 a = new Argument1();
		a.setData("AAAA");
		ClassMapping classMapping = new ClassMapping();
		Result ret = classMapping.map(null, Result.class, a);
		System.out.println(ret);
	}

}
