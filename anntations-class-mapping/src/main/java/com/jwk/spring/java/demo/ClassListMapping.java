package com.jwk.spring.java.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ClassListMapping")
public class ClassListMapping implements Mapping {

	@Override
	public <R> R map(R r, Class<R> type, Object... objects) {
		if(objects[0] instanceof List) {
			
		}
		return null;
	}

}
