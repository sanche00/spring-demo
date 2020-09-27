package com.jwk.spring.java.demo.test;

import com.jwk.spring.java.demo.Condition;

public class TestCondition<T> implements Condition<T> {

	@Override
	public boolean isCondition(T src) {
		return false;
	}



}
