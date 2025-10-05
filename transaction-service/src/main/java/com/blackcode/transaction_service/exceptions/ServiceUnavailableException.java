package com.blackcode.transaction_service.exceptions;

public class ServiceUnavailableException extends RuntimeException{
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
