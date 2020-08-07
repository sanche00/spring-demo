package com.jwk.spring.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwk.spring.demo.dto.FirstAragument;
import com.jwk.spring.demo.dto.SecondAragument;
import com.jwk.spring.demo.service.EventAopService;

@RestController
public class HomeController {

	@Autowired
	@Qualifier("eventService1")
	EventAopService eventAopService1;
	
	@Autowired
	@Qualifier("eventService2")
	EventAopService eventAopService2;
	
	@GetMapping("aop1")
	public void get() {
		eventAopService1.execute(FirstAragument.of("1", "a"), SecondAragument.of("2", "b"));
	}
	
	@GetMapping("aop2")
	public void get2() {
		eventAopService2.execute(FirstAragument.of("1", "a"), SecondAragument.of("2", "b"));
	}
}
