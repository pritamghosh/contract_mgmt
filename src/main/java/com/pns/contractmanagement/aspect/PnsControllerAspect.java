package com.pns.contractmanagement.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PnsControllerAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(PnsControllerAspect.class);

	@Before("execution(* com.pns.contractmanagement.controller..*(..))")
	public void beforeAdvice(JoinPoint joinPoint) throws Throwable {
		LOGGER.debug("Executing Method {} .", joinPoint.getSignature());
	}
	
	@AfterReturning("execution(* com.pns.contractmanagement.controller..*(..))")
	public void afterAdvice(JoinPoint joinPoint) throws Throwable {
		LOGGER.debug("Exiting Method {} .", joinPoint.getSignature());
	}
}
