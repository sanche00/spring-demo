package com.jwk.spring.java.demo.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class RestUtils {

	private final static String PATH_VAR_FMT = "{%s}";

	public static String createPath(String fmt, Object request) {
		String ret = fmt;
		Field fields[] = request.getClass().getDeclaredFields();
		for (Field field : fields) {
			PathVar[] pathVars = field.getAnnotationsByType(PathVar.class);
			if (pathVars == null || pathVars.length == 0) {
				continue;
			}

			PathVar pathVar = pathVars[0];
			String key = null;
			if (pathVar.value().isEmpty()) {
				key = String.format(PATH_VAR_FMT, field.getName());
			} else {
				key = String.format(PATH_VAR_FMT, pathVar.value());
			}
			try {
				field.setAccessible(true);
				ret = ret.replace(key, String.valueOf(field.get(request)));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}finally {
				field.setAccessible(false);
			}
		}
		return ret;
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	static public @interface PathVar {
		String value() default "";
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	static public @interface ParamVar {
		String value() default "";
	}

}
