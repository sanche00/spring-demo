package com.jwk.spring.java.demo.test;

import com.jwk.spring.java.demo.ClassMap;
import com.jwk.spring.java.demo.ClassMap.ClassType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result2 {
	
	@ClassMap(classes = {Result.class}, value = "retxxx")
	String ret2;
	
	@ClassMap(classes = {Result.class}, value = "result" , type = ClassType.OBJECT)
	Argument1 xx;
	
	@ClassMap(classes = {Result.class}, value = "result2.ret")
	String retxxx;
	
	
}
