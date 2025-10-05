package com.blackcode.financial_plan_service.service;

import com.blackcode.financial_plan_service.dto.RollbackRes;
import com.blackcode.financial_plan_service.dto.TransactionRes;

import java.util.List;

public interface TransactionClientService {

    List<TransactionRes> getTransactionByPlan(String userId, String planId);

    boolean deleteTransaction(String userId, String planId);

    RollbackRes rollbackTransaction(String userId, String planId);
}
