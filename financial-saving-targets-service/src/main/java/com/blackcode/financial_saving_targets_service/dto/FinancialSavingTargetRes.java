package com.blackcode.financial_saving_targets_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialSavingTargetRes {

    private String targetId;

    private String planId;

    private String userId;

    private String name;

    private String amount;

    private String notes;

}
