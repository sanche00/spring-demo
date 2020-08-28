package com.jwk.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class RestDocApplication {

	public static void main(String[] args) {
		System.out.println("zzz");
		SpringApplication.run(RestDocApplication.class, args);
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
