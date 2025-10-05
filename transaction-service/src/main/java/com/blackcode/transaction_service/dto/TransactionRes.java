package com.blackcode.transaction_service.dto;

import com.blackcode.transaction_service.model.Category;
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

    private CategoryRes categories;

    private String amount;

    private String paymentType;

    private String description;



}
