package com.blackcode.financial_evaluations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialEvaluationsRes {

    private String evaluationId;

    private String planId;

    private String userId;

    private String netBalance;

    private String targetAmount;

    private boolean isAchievable;

    private LocalDateTime evaluatedAt;

}
