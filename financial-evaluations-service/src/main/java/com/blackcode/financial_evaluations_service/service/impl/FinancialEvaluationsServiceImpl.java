package com.blackcode.financial_evaluations_service.service.impl;

import com.blackcode.financial_evaluations_service.dto.FinancialEvaluationsRes;
import com.blackcode.financial_evaluations_service.dto.FinancialPlanRes;
import com.blackcode.financial_evaluations_service.dto.SavingTargetRes;
import com.blackcode.financial_evaluations_service.dto.TransactionRes;
import com.blackcode.financial_evaluations_service.exceptions.DataNotFoundException;
import com.blackcode.financial_evaluations_service.model.FinancialEvaluations;
import com.blackcode.financial_evaluations_service.repository.FinancialEvaluationsRepository;
import com.blackcode.financial_evaluations_service.service.FinancialEvaluationsService;
import com.blackcode.financial_evaluations_service.service.FinancialPlaneClientService;
import com.blackcode.financial_evaluations_service.service.SavingTargetClientService;
import com.blackcode.financial_evaluations_service.service.TransactionClientService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class FinancialEvaluationsServiceImpl implements FinancialEvaluationsService {

    private final FinancialEvaluationsRepository financialEvaluationsRepository;

    private final FinancialPlaneClientService financialPlaneClientService;

    private final SavingTargetClientService savingTargetClientService;

    private final TransactionClientService transactionClientService;

    public FinancialEvaluationsServiceImpl(FinancialEvaluationsRepository financialEvaluationsRepository, FinancialPlaneClientService financialPlaneClientService, SavingTargetClientService savingTargetClientService, TransactionClientService transactionClientService) {
        this.financialEvaluationsRepository = financialEvaluationsRepository;
        this.financialPlaneClientService = financialPlaneClientService;
        this.savingTargetClientService = savingTargetClientService;
        this.transactionClientService = transactionClientService;
    }


    @Override
    public FinancialEvaluationsRes getEvaluationsPlan(String userId, String planId) {
        FinancialEvaluations financialEvaluation = financialEvaluationsRepository.findByUserIdAndPlanId(userId, planId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));

        return mapToFinancialEvaluationsRes(financialEvaluation);
    }

    @Override
    public FinancialEvaluationsRes createEvaluationsPlan(String userId, String planId) {
        FinancialEvaluations financialEvaluations = new FinancialEvaluations();
        financialEvaluations.setEvaluationId(UUID.randomUUID().toString());
        financialEvaluations.setUserId(userId);

        FinancialPlanRes financialPlan = financialPlaneClientService.getFinancialPlanById(userId, planId);
        if (financialPlan == null) {
            throw new DataNotFoundException("FinancialPlan Not Found with id : " + planId);
        }
        financialEvaluations.setPlanId(financialPlan.getPlanId());

        List<SavingTargetRes> savingTargetList = savingTargetClientService.getFinancialSavingTargetByPlan(userId, planId);
        if (savingTargetList == null || savingTargetList.isEmpty()) {
            throw new DataNotFoundException("SavingTarget Not Found with id : " + planId);
        }

        List<TransactionRes> transactionList = transactionClientService.getTransactionByPlan(userId, planId);
        if (transactionList == null || transactionList.isEmpty()) {
            throw new DataNotFoundException("Transaction Not Found with id : " + planId);
        }

        BigDecimal totalIncome = transactionList.stream()
                .filter(tx -> "PEMASUKAN".equalsIgnoreCase(tx.getCategories().getCategoryType()))
                .map(tx -> safeToBigDecimal(tx.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionList.stream()
                .filter(tx -> "PENGELUARAN".equalsIgnoreCase(tx.getCategories().getCategoryType()))
                .map(tx -> safeToBigDecimal(tx.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);
        financialEvaluations.setNetBalance(netBalance.toPlainString());

        BigDecimal targetAmount = savingTargetList.stream()
                .map(item -> safeToBigDecimal(item.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        financialEvaluations.setTargetAmount(targetAmount.toPlainString());
        financialEvaluations.setAchievable(netBalance.compareTo(targetAmount) >= 0);
        financialEvaluations.setEvaluatedAt(LocalDateTime.now());
        FinancialEvaluations financialEvaluationsSave =  financialEvaluationsRepository.save(financialEvaluations);

        return mapToFinancialEvaluationsRes(financialEvaluationsSave);
    }

    private BigDecimal safeToBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }


    private FinancialEvaluationsRes mapToFinancialEvaluationsRes(FinancialEvaluations financialEvaluations){
        FinancialEvaluationsRes financialEvaluationsRes = new FinancialEvaluationsRes();
        financialEvaluationsRes.setEvaluationId(financialEvaluations.getEvaluationId());
        financialEvaluationsRes.setPlanId(financialEvaluations.getPlanId());
        financialEvaluationsRes.setUserId(financialEvaluations.getUserId());
        financialEvaluationsRes.setNetBalance(financialEvaluations.getNetBalance());
        financialEvaluationsRes.setTargetAmount(financialEvaluations.getTargetAmount());
        financialEvaluationsRes.setAchievable(financialEvaluations.isAchievable());
        financialEvaluationsRes.setEvaluatedAt(financialEvaluations.getEvaluatedAt());
        return financialEvaluationsRes;
    }
}