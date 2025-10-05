package com.blackcode.financial_evaluations_service.service;


import com.blackcode.financial_evaluations_service.dto.RollbackRes;
import com.blackcode.financial_evaluations_service.dto.TransactionRes;

import java.util.List;

public interface TransactionClientService {

    List<TransactionRes> getTransactionByPlan(String userId, String planId);

}
