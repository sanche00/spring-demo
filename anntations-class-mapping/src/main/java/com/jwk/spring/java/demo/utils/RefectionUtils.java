package com.jwk.spring.java.demo.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class RefectionUtils {

	public static <R extends Annotation> R[] getAnnotationsByType(Field field, Object object, Class<R> type) {
		return null;
	}
	
	public static void setFieldValue(Field field, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} finally {
			field.setAccessible(false);
		}
	}
	
	public static Object getFieldValue(Field field, Object obj) throws IllegalArgumentException, IllegalAccessException {
		try {
			field.setAccessible(true);
			return field.get(obj);
		} finally {
			field.setAccessible(false);
		}
	}
}
