package com.jwk.spring.java.demo;

public interface Mapping {
	Object map(Object r, Class<?> type, Object... objects)  ;
	
	Object map(Class<?> type, Object... objects)   ;
}
