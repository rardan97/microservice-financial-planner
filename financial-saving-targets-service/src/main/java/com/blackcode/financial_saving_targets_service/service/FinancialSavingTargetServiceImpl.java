package com.blackcode.financial_saving_targets_service.service;

import com.blackcode.financial_saving_targets_service.dto.*;
import com.blackcode.financial_saving_targets_service.exceptions.DataNotFoundException;
import com.blackcode.financial_saving_targets_service.model.FinancialSavingTarget;
import com.blackcode.financial_saving_targets_service.repository.FinancialSavingTargetRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FinancialSavingTargetServiceImpl implements FinancialSavingTargetService{

    private final FinancialSavingTargetRepository financialSavingTargetRepository;

    public FinancialSavingTargetServiceImpl(FinancialSavingTargetRepository financialSavingTargetRepository) {
        this.financialSavingTargetRepository = financialSavingTargetRepository;
    }

    @Override
    public List<FinancialSavingTargetRes> getFinancialSavingTargetByPlan(String userId, String planId) {
        List<FinancialSavingTarget> financialSavingTargets = financialSavingTargetRepository.findByUserIdAndPlanId(userId, planId);

        if (financialSavingTargets == null) {
            return Collections.emptyList();
        }

        return financialSavingTargets.stream()
                .map(this::mapToFinancialSavingTargetRes).toList();
    }

    @Override
    public FinancialSavingTargetRes getFinancialSavingTargetsById(String userId, String planId, String targetId) {
        FinancialSavingTarget financialSavingTarget = financialSavingTargetRepository.findByUserIdAndPlanIdAndTargetId(userId, planId, targetId)
                .orElseThrow(() -> new DataNotFoundException("financialSavingTarget Not Found with id : "+planId));
        return mapToFinancialSavingTargetRes(financialSavingTarget);
    }

    @Override
    public FinancialSavingTargetRes crateFinancialSavingTarget(String userId, String planId, FinancialSavingTargetReq financialSavingTargetReq) {
        FinancialSavingTarget financialSavingTarget = new FinancialSavingTarget();
        financialSavingTarget.setTargetId(UUID.randomUUID().toString());
        financialSavingTarget.setPlanId(financialSavingTargetReq.getPlanId());
        financialSavingTarget.setUserId(userId);
        financialSavingTarget.setName(financialSavingTargetReq.getName());
        financialSavingTarget.setAmount(financialSavingTargetReq.getAmount());
        financialSavingTarget.setNotes(financialSavingTargetReq.getNotes());
        financialSavingTarget.setDeleted(false);
        FinancialSavingTarget financialSavingTarget1 = financialSavingTargetRepository.save(financialSavingTarget);
        return mapToFinancialSavingTargetRes(financialSavingTarget1);
    }

    @Override
    public FinancialSavingTargetRes updateFinancialSavingTarget(String userId, String planId, String targetId, FinancialSavingTargetReq financialSavingTargetReq) {
        FinancialSavingTarget financialSavingTarget = financialSavingTargetRepository.findByUserIdAndPlanIdAndTargetId(userId, planId, targetId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));
        financialSavingTarget.setName(financialSavingTargetReq.getName());
        financialSavingTarget.setAmount(financialSavingTargetReq.getAmount());
        financialSavingTarget.setNotes(financialSavingTargetReq.getNotes());

        FinancialSavingTarget updateFinancialSavingTarget = financialSavingTargetRepository.save(financialSavingTarget);
        return mapToFinancialSavingTargetRes(updateFinancialSavingTarget);
    }

    @Override
    public MessageRes deleteSavingTargetByPlan(String userId, String planId) {
        if (planId == null || planId.isEmpty() || userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("PlanId and UserId must not be null or empty");
        }

        MessageRes messageRes = new MessageRes();
        List<FinancialSavingTarget> financialSavingTarget = financialSavingTargetRepository.findByUserIdAndPlanId(userId, planId);

        if (financialSavingTarget == null || financialSavingTarget.isEmpty()) {
            messageRes.setMessage("No transactions found for this plan.");
            return messageRes;
        }

        for (FinancialSavingTarget tx : financialSavingTarget) {
            tx.setDeleted(true);
            tx.setDeleteReason("BY_PLAN");
        }

        financialSavingTargetRepository.saveAll(financialSavingTarget);

        messageRes.setStatus(true);
        messageRes.setMessage("Transactions related to the plan were deleted");
        return messageRes;
    }

    @Override
    public MessageRes deleteSavingTargetById(String userId, String planId, String targetId) {
        if (planId == null || planId.isEmpty() || userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("PlanId and UserId must not be null or empty");
        }

        FinancialSavingTarget financialSavingTarget = financialSavingTargetRepository.findByUserIdAndPlanIdAndTargetId(userId, planId, targetId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));

        financialSavingTarget.setDeleted(true);
        financialSavingTarget.setDeleteReason("MANUAL");
        financialSavingTargetRepository.save(financialSavingTarget);
        MessageRes messageRes = new MessageRes();
        messageRes.setStatus(true);
        messageRes.setMessage("The Saving Target was removed from the database");
        return messageRes;
    }

    @Override
    public RollbackRes rollbackFinancialSavingTarget(String userId, RollbackReq rollbackReq) {
        List<FinancialSavingTarget> deleteFinancialSavingTarget = financialSavingTargetRepository
                .findByUserIdAndPlanIdAndDeletedTrueAndDeleteReason(userId, rollbackReq.getPlanId(), "BY_PLAN");

        if (deleteFinancialSavingTarget.isEmpty()) {
            return new RollbackRes(false, "ROLLBACK", "Tidak ada transaksi yang dihapus dengan planId: " + rollbackReq.getPlanId());
        }

        for (FinancialSavingTarget tx : deleteFinancialSavingTarget) {
            tx.setDeleted(false);
            tx.setDeleteReason(null);
            financialSavingTargetRepository.save(tx);
        }

        return new RollbackRes(true, "ROLLBACK", "Rollback delete transaksi by plan berhasil untuk planId: " + rollbackReq.getPlanId());
    }

    private FinancialSavingTargetRes mapToFinancialSavingTargetRes(FinancialSavingTarget financialSavingTarget){
        FinancialSavingTargetRes financialSavingTargetRes = new FinancialSavingTargetRes();
        financialSavingTargetRes.setTargetId(financialSavingTarget.getTargetId());
        financialSavingTargetRes.setPlanId(financialSavingTarget.getPlanId());
        financialSavingTargetRes.setUserId(financialSavingTarget.getUserId());
        financialSavingTargetRes.setName(financialSavingTarget.getName());
        financialSavingTargetRes.setAmount(financialSavingTarget.getAmount());
        financialSavingTargetRes.setNotes(financialSavingTarget.getNotes());
        return financialSavingTargetRes;
    }
}
