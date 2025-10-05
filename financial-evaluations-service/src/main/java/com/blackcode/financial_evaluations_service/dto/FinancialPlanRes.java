package com.blackcode.financial_evaluations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialPlanRes {

    private String planId;

    private String userId;

    private String planName;

    private Date startDate;

    private Date endDate;

    private String notes;

    private List<SavingTargetRes> savingTarget;

    private List<TransactionRes> transactionRes;

}
