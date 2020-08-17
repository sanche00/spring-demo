package com.jwk.spring.java.demo;

public class DefaultFormatter implements Formatter<Object, Object>{

	@Override
	public Object format(Object t) {
		return t;
	}

}
