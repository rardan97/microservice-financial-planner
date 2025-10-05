package com.blackcode.transaction_service.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionReq {

    private String planId;

    private String transactionName;

    private Date transactionDate;

    private String categories;

    private String amount;

    private String paymentType;

    private String description;
}
