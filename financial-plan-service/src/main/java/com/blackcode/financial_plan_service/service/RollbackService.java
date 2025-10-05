package com.blackcode.financial_plan_service.service;

import com.blackcode.financial_plan_service.model.FinancialPlan;

public interface RollbackService {
    void rollbackDeleteFinancialPlan(String userId, FinancialPlan plan);
}
