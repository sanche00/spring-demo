package com.jwk.spring.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwk.spring.demo.dto.RestDocDemoDTO;
import com.jwk.spring.demo.service.RestDocService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/restDoc")
@Slf4j
public class RestDocDemoController {
	
//	@Qualifier("restDocService")
	@Autowired
	RestDocService restDocService;
	
	@GetMapping("/get/{id}")
	public RestDocDemoDTO.GetRes get(@PathVariable("id") String id) {
		log.info("rest Doc Get Call !!!");
		return restDocService.get(id);
	}
	
	@PostMapping("/post/{id}")
	public RestDocDemoDTO.PostRes post(@PathVariable("id") String id, @RequestBody RestDocDemoDTO.PostReq req) {
		log.info("rest Doc post Call !!!");
		return restDocService.post(id, req);
	}
	
	@PutMapping("/put/{id}")
	public RestDocDemoDTO.PutRes put(@PathVariable("id") String id,  @RequestBody RestDocDemoDTO.PutReq req) {
		log.info("rest Doc Put Call !!!");
		return restDocService.put(id, req);
	}
}
