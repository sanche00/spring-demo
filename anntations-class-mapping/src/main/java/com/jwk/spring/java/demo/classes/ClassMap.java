package com.jwk.spring.java.demo.classes;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.jwk.spring.java.demo.Condition;
import com.jwk.spring.java.demo.DefaultFormatter;
import com.jwk.spring.java.demo.SPFormatter;


@Repeatable( value = ClassMaps.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassMap {

	Class<?>[] classes();
	Class<? extends SPFormatter<?,?>> format() default DefaultFormatter.class;
	boolean reversFmt() default false;
	//조건처리 필요
	
	Class<?> reversListInputType() default Object.class;
	boolean isReverseMethod() default false;
	String value() ;
	ClassType type() default ClassType.NONE;
	String listClassName() default "";
	static enum ClassType {
		NONE, LIST, OBJECT
	}
	
	Class<? extends Condition> conditonClass() default Condition.class;
	String[] conditionValue() default {};
	String conditionMethod() default "";
	
	
	//value 가 전체 클래스르 바라봄 (object일때 사용)
	public static final String THIS = "this";


}
