package com.blackcode.financial_saving_targets_service.exceptions;

public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException(String message){
        super(message);
    }
}
