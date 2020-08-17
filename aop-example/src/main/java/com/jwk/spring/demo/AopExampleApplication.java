package com.jwk.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ServletComponentScan
public class AopExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AopExampleApplication.class, args);
	}

}
