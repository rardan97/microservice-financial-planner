package com.blackcode.financial_plan_service.service.impl;

import com.blackcode.financial_plan_service.dto.RollbackRes;
import com.blackcode.financial_plan_service.model.FinancialPlan;
import com.blackcode.financial_plan_service.repository.FinancialPlanRepository;
import com.blackcode.financial_plan_service.service.RollbackService;
import com.blackcode.financial_plan_service.service.SavingTargetClientService;
import com.blackcode.financial_plan_service.service.TransactionClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RollbackServiceImpl implements RollbackService {

    private static final Logger logger = LoggerFactory.getLogger(RollbackServiceImpl.class);

    private final FinancialPlanRepository financialPlanRepository;
    private final TransactionClientService transactionClientService;
    private final SavingTargetClientService savingTargetClientService;

    public RollbackServiceImpl(FinancialPlanRepository financialPlanRepository, TransactionClientService transactionClientService, SavingTargetClientService savingTargetClientService) {
        this.financialPlanRepository = financialPlanRepository;
        this.transactionClientService = transactionClientService;
        this.savingTargetClientService = savingTargetClientService;
    }


    @Override
    public void rollbackDeleteFinancialPlan(String userId, FinancialPlan plan) {
        rollbackSoftDelete(plan);
        rollbackTransaction(userId, plan.getPlanId());
        rollbackSavingTarget(userId, plan.getPlanId());
    }

    private void rollbackSoftDelete(FinancialPlan plan) {
        try {
            if (plan.isDeleted()) {
                plan.setDeleted(false);
                financialPlanRepository.save(plan);
                logger.info("Rollback soft delete success: {}", plan.getPlanId());
            }
        } catch (Exception ex) {
            logger.error("Failed to rollback soft delete for planId: {}", plan.getPlanId(), ex);
        }
    }

    private void rollbackTransaction(String userId, String planId) {
        try {
            RollbackRes res = transactionClientService.rollbackTransaction(userId, planId);
            logger.info("Rollback transaction: {}", res.getMessage());
        } catch (Exception ex) {
            logger.error("Failed to rollback transaction for planId: {}", planId, ex);
        }
    }

    private void rollbackSavingTarget(String userId, String planId) {
        try {
            RollbackRes res = savingTargetClientService.rollbackSavingTarget(userId, planId);
            logger.info("Rollback saving target: {}", res.getMessage());
        } catch (Exception ex) {
            logger.error("Failed to rollback saving target for planId: {}", planId, ex);
        }
    }
}
