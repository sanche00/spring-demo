package com.jwk.spring.java.demo.warp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MyMapper {
	@Select("SELECT test1 FROM public.tbl_test_clob limit 1;")
	String findDbName();
}
