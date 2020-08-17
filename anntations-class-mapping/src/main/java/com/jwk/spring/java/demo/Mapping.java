package com.jwk.spring.java.demo;

public interface Mapping {
	<R> R map(R r, Class<R> type, Object... objects);
}
