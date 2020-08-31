package com.jwk.spring.java.demo.test;

import java.util.ArrayList;
import java.util.List;

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
public class Argument1 {

	@ClassMap(classes = {Result.class}, value = "ret")
	private String data;
	
	@ClassMap(classes = {Result.class}, value = "sss" , type = ClassType.LIST , listClassName = "com.jwk.spring.java.demo.test.Result")
	private List<Argument1> datas = new ArrayList<Argument1>();
}
