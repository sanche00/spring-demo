package com.jwk.spring.demo.service;

import org.springframework.stereotype.Service;

import com.jwk.spring.demo.dto.RestDocDemoDTO.GetRes;
import com.jwk.spring.demo.dto.RestDocDemoDTO.PostReq;
import com.jwk.spring.demo.dto.RestDocDemoDTO.PostRes;
import com.jwk.spring.demo.dto.RestDocDemoDTO.PutReq;
import com.jwk.spring.demo.dto.RestDocDemoDTO.PutRes;

@Service("restDocService")
public class RestDocServiceImpl implements RestDocService {

	@Override
	public GetRes get(String id) {
		// TODO Auto-generated method stub
		return GetRes.builder().id("id").idx(1).name("name").build();
	}

	@Override
	public PostRes post(String id, PostReq req) {
		// TODO Auto-generated method stub
		return  PostRes.builder().id("id").idx(1).name("name").build();
	}

	@Override
	public PutRes put(String id, PutReq req) {
		// TODO Auto-generated method stub
		return PutRes.builder().id("id").idx(1).name("name").build();
	}

}
