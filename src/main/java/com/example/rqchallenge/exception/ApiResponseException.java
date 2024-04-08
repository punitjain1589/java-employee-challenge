package com.example.rqchallenge.exception;

public class ApiResponseException extends RuntimeException {

    public ApiResponseException(String message) {
        super(message);
    }
}
