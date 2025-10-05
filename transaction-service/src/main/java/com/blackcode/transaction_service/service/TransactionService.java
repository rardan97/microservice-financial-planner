package com.blackcode.transaction_service.service;

import com.blackcode.transaction_service.dto.*;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    List<TransactionRes> getTransactionByPlan(String userId, String planId);


    TransactionRes getTransactionAllById(String userId, String planId, String transactionId);

    TransactionRes createTransaction(String userId, String planId, TransactionReq transactionReq);

    TransactionRes updateTransaction(String userId, String planId, String transactionId, TransactionReq transactionReq);

    MessageRes deleteTransactionByPlan(String userId, String planId);

    MessageRes deleteTransactionById(String userId, String planId, String transactionId);

    RollbackRes rollbackTransactionByPlan(String userId, RollbackReq rollbackReq);
}
