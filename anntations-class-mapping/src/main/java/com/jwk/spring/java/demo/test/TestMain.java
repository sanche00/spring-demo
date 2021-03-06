package com.jwk.spring.java.demo.test;

import java.util.ArrayList;
import java.util.List;

import com.jwk.spring.java.demo.classes.ClassMapping;

public class TestMain {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		Argument1 a = new Argument1();
		a.setData("AAAA");
		a.getDatas().add(new Argument1("ZZZZ", null));
		a.getDatas().add(new Argument1("xxx", null));
		ClassMapping classMapping = new ClassMapping();
		Result ret = (Result) classMapping.map(Result.class, a, Result2.builder().ret2("bbbb").xx(a).build());
		System.out.println(ret);
		
		List<Argument1> list = new ArrayList<Argument1>();
		list.add(a);
		list.add(Argument1.builder().data("zz").build());
		list.add(Argument1.builder().data("xx").build());
		list.add(Argument1.builder().data("yy").build());
		Object res = classMapping.map(Result.class, list, Result2.builder().ret2("bbbb").retxxx("bb1111111111b").build());
		
		System.out.println(res);
	}

}
