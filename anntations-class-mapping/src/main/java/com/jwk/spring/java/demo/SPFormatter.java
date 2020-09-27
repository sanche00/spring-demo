package com.jwk.spring.java.demo;

public interface SPFormatter<T,R> {
	R format(T t);
	T parse(R r);
}
