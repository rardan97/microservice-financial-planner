package com.blackcode.financial_saving_targets_service.service;

import com.blackcode.financial_saving_targets_service.dto.*;

import java.util.List;
import java.util.Map;

public interface FinancialSavingTargetService {

    List<FinancialSavingTargetRes> getFinancialSavingTargetByPlan(String userId, String planId);

    FinancialSavingTargetRes getFinancialSavingTargetsById(String userId, String planId, String targetId);

    FinancialSavingTargetRes crateFinancialSavingTarget(String userId, String planId, FinancialSavingTargetReq financialSavingTargetReq);

    FinancialSavingTargetRes updateFinancialSavingTarget(String userId, String planId, String targetId, FinancialSavingTargetReq financialPlanReq);

    MessageRes deleteSavingTargetByPlan(String userId, String planId);

    MessageRes deleteSavingTargetById(String userId, String planId, String targetId);

    RollbackRes rollbackFinancialSavingTarget(String userId, RollbackReq rollbackReq);
}
