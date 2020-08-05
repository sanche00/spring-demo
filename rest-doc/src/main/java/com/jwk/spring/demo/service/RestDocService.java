package com.jwk.spring.demo.service;

import com.jwk.spring.demo.dto.RestDocDemoDTO;

public interface RestDocService {
	RestDocDemoDTO.GetRes get(String id);
	RestDocDemoDTO.PostRes post(String id, RestDocDemoDTO.PostReq req) ;
	RestDocDemoDTO.PutRes put(String id, RestDocDemoDTO.PutReq req) ;
}
