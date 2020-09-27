package com.jwk.spring.java.demo.warp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MyMapper {
	@Select("SELECT small_cd FROM public.tba_smallllf_m limit 1")
	String findDbName();
}
