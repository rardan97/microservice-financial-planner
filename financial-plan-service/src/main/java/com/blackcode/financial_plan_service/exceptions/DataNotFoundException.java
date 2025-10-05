package com.blackcode.financial_plan_service.exceptions;

public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException(String message){
        super(message);
    }
}
