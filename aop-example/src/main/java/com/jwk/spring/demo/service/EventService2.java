package com.jwk.spring.demo.service;

import org.springframework.stereotype.Service;

import com.jwk.spring.demo.dto.FirstAragument;
import com.jwk.spring.demo.dto.SecondAragument;

import lombok.extern.slf4j.Slf4j;

@Service("eventService2")
@Slf4j
public class EventService2 implements EventAopService {

	@Override
	public String execute(FirstAragument t, SecondAragument t2) {
		log.info("Call EventService2");
		return t.getId();
	}

}
