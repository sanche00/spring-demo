package com.jwk.spring.java.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassMap {
	Class<?>[] classes();
	Class<? extends Formatter<?,?>> format() default DefaultFormatter.class;
	String value() ;
	ClassType type() default ClassType.NONE;
	String listClassName() default "";
	static enum ClassType {
		NONE, LIST, OBJECT
	}
}
