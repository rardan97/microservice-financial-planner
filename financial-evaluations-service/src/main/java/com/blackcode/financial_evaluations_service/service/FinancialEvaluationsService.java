package com.blackcode.financial_evaluations_service.service;

import com.blackcode.financial_evaluations_service.dto.FinancialEvaluationsRes;

import java.util.List;

public interface FinancialEvaluationsService {

    List<FinancialEvaluationsRes> getEvaluationsAll(String userId);

    FinancialEvaluationsRes getTransactionByPlan(String userId, String planId);

    FinancialEvaluationsRes createEvaluationsPlan(String userId, String planId);
}
