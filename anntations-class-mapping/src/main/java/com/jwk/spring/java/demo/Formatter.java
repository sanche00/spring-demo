package com.jwk.spring.java.demo;

public interface Formatter<T,R> {
	R format(T t);
}
