package com.jwk.spring.java.demo;

public interface Mapping {
	Object map(Object r, Class<?> type, Object... objects) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException  ;
	
	Object map(Class<?> type, Object... objects) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException  ;
}
