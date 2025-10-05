package com.blackcode.financial_evaluations_service.service;


import com.blackcode.financial_evaluations_service.dto.RollbackRes;
import com.blackcode.financial_evaluations_service.dto.SavingTargetRes;

import java.util.List;

public interface SavingTargetClientService {

    List<SavingTargetRes> getFinancialSavingTargetByPlan(String userId, String planId);

}
