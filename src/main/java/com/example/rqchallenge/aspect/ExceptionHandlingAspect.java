package com.example.rqchallenge.aspect;

import com.example.rqchallenge.exception.ApiResponseException;
import com.example.rqchallenge.exception.DataNotFoundException;
import com.example.rqchallenge.exception.TooManyRequestException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;

@Aspect
@Component
@Slf4j
public class ExceptionHandlingAspect {

    @AfterThrowing(pointcut = "@annotation(com.example.rqchallenge.annotation.EnableExceptionHandling)", throwing = "exception")
    public void handleException(JoinPoint joinPoint, Exception exception) throws Throwable {
        log.error("Exception occurred in {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
        switch (exception.getClass().getCanonicalName()) {
            case "com.example.rqchallenge.exception.ApiResponseException", "feign.FeignException.InternalServerError" -> throw new ApiResponseException("Failed to get the response from API");
            case "feign.RetryableException", "feign.FeignException.TooManyRequests" -> throw new TooManyRequestException("API is unable to take the request");
            case "com.example.rqchallenge.exception.DataNotFoundException" -> throw new DataNotFoundException("No Data found on the API for given selector");
            case "javax.validation.ConstraintViolationException" -> throw exception;
            default -> throw new Exception("Internal Server Error");
        }
    }
}
