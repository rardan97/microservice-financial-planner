package com.blackcode.financial_saving_targets_service.exceptions;

public class ExternalServiceException extends RuntimeException{
    public ExternalServiceException(String message) {
        super(message);
    }
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
