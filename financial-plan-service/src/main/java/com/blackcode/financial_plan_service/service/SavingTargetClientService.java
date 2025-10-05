package com.blackcode.financial_plan_service.service;

import com.blackcode.financial_plan_service.dto.RollbackRes;
import com.blackcode.financial_plan_service.dto.SavingTargetRes;

import java.util.List;

public interface SavingTargetClientService {

    List<SavingTargetRes> getFinancialSavingTargetByPlan(String userId, String planId);

    boolean deleteSavingTarget(String userId, String planId);

    RollbackRes rollbackSavingTarget(String userId, String planId);
}
