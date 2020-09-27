package com.jwk.spring.java.demo;

public class DefaultFormatter implements SPFormatter<Object, Object>{

	@Override
	public Object format(Object t) {
		return t;
	}

	@Override
	public Object parse(Object r) {
		// TODO Auto-generated method stub
		return r;
	}

}
