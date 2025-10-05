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
public class FinancialPlanReq {

    private String userId;

    private String planName;

    private Date startDate;

    private Date endDate;

    private String notes;


}
