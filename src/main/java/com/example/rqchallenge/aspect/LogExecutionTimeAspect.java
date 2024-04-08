package com.example.rqchallenge.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogExecutionTimeAspect {

    @Around("@annotation(com.example.rqchallenge.annotation.EnableExecutionTimeLogging)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long totalTime = System.currentTimeMillis() - startTime;
        log.info("{} executed in {} ms", joinPoint.getSignature().toShortString(), totalTime);
        return proceed;
    }
}
