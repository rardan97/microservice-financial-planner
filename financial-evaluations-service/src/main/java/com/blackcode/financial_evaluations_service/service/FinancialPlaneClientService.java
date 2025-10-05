package com.blackcode.financial_evaluations_service.service;

import com.blackcode.financial_evaluations_service.dto.FinancialPlanRes;

public interface FinancialPlaneClientService {

    FinancialPlanRes getFinancialPlanById(String userId, String planId);
}
