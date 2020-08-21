package com.pns.contractmanagement.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PnsControllerAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(PnsControllerAspect.class);

	@Around("execution(* com.pns.contractmanagement.controller.*(..))")
	public void beforeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		LOGGER.info("Executing Method {} with parameters {} .", joinPoint.getSignature(), joinPoint.getArgs());
		joinPoint.proceed();
		LOGGER.info("Exiting Method {} .", joinPoint.getSignature());
	}
}
