package com.jwk.spring.demo.service;

import com.jwk.spring.demo.dto.FirstAragument;
import com.jwk.spring.demo.dto.SecondAragument;

public interface EventAopService {
	<T extends FirstAragument, T2 extends SecondAragument> String  execute(T t, T2 t2);
}
