package com.jwk.spring.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class FirstAragument {
	String id;
	String name;
}
