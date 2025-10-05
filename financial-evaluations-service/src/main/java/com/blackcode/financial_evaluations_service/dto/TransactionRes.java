package com.blackcode.financial_evaluations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionRes {

    private String transactionId;

    private String userId;

    private String planId;

    private Date transactionDate;

    private String categories;

    private String amount;

    private String paymentType;

    private String description;

}
