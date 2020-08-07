package com.jwk.spring.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.jwk.spring.demo.dto.FirstAragument;
import com.jwk.spring.demo.dto.SecondAragument;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class EventAapect {

	@Around("execution(* com.jwk.spring.demo.service.EventAopService.execute(..))")
	public Object catchEventMethod(ProceedingJoinPoint pjp) throws Throwable {
		log.info("hook method : {}", pjp.getTarget().getClass().getName());
		FirstAragument arg1 = FirstAragument.class.cast(pjp.getArgs()[0]);
		SecondAragument arg2 = SecondAragument.class.cast(pjp.getArgs()[1]);
		arg1.setId(arg2.getId());
		Object retVal = pjp.proceed(); // 메서드 호출 자체를 감쌈
		return retVal;
	}

}
