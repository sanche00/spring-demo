package com.jwk.spring.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.jwk.spring.demo.dto.FirstAragument;
import com.jwk.spring.demo.dto.SecondAragument;
import com.jwk.spring.demo.service.EventAopService;

@SpringBootTest
class AopExampleApplicationTests {

	@Autowired
	@Qualifier("eventService1")
	EventAopService eventAopService1;
	
	@Autowired
	@Qualifier("eventService2")
	EventAopService eventAopService2;
	
	@Test
	void service1Test() {
		String ret = "2";
		String result = eventAopService1.execute(FirstAragument.of("1", "a"), SecondAragument.of(ret, "b"));
		assertEquals(ret, result);
	}

}
