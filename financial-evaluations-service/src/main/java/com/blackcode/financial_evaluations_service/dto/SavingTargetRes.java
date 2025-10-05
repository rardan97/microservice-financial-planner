package com.blackcode.financial_evaluations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavingTargetRes {

    private String targetId;

    private String planId;

    private String userId;

    private String name;

    private String amount;

    private String notes;

}
