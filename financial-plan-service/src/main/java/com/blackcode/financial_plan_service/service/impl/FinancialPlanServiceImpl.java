package com.blackcode.financial_plan_service.service.impl;

import com.blackcode.financial_plan_service.dto.*;
import com.blackcode.financial_plan_service.exceptions.DataNotFoundException;
import com.blackcode.financial_plan_service.exceptions.ServiceUnavailableException;
import com.blackcode.financial_plan_service.model.FinancialPlan;
import com.blackcode.financial_plan_service.repository.FinancialPlanRepository;
import com.blackcode.financial_plan_service.service.FinancialPlanService;
import com.blackcode.financial_plan_service.service.RollbackService;
import com.blackcode.financial_plan_service.service.SavingTargetClientService;
import com.blackcode.financial_plan_service.service.TransactionClientService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FinancialPlanServiceImpl implements FinancialPlanService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialPlanServiceImpl.class);

    private final FinancialPlanRepository financialPlanRepository;

    private final SavingTargetClientService savingTargetClientService;

    private final TransactionClientService transactionClientService;

    private final RollbackService rollbackService;

    public FinancialPlanServiceImpl(FinancialPlanRepository financialPlanRepository, SavingTargetClientService savingTargetClientService, TransactionClientService transactionClientService, RollbackService rollbackService) {
        this.financialPlanRepository = financialPlanRepository;
        this.savingTargetClientService = savingTargetClientService;
        this.transactionClientService = transactionClientService;
        this.rollbackService = rollbackService;
    }

    @Override
    public List<FinancialPlanRes> getFinancialPlanAll(String userId) {
        List<FinancialPlan> financialPlanList = financialPlanRepository.findPlanByUserId(userId);
        if(financialPlanList.isEmpty()){
            throw new DataNotFoundException("Data FinancialPlan Not Found");
        }
        return financialPlanList.stream()
                .map(this::mapToFinancialPlanRes).toList();
    }

    @Override
    public FinancialPlanRes getFinancialPlanById(String userId, String planId) {
        FinancialPlan financialPlan = financialPlanRepository.findByPlanIdAndUserId(planId, userId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));

        List<SavingTargetRes> savingTargetList = savingTargetClientService.getFinancialSavingTargetByPlan(userId, planId);
        List<TransactionRes> transactionList = transactionClientService.getTransactionByPlan(userId, planId);
        return mapToFinancialPlanDetailRes(financialPlan, savingTargetList, transactionList);
    }

    @Transactional
    @Override
    public FinancialPlanRes createFinancialPlan(String userId, FinancialPlanReq financialPlanReq) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("PlanId and UserId must not be null or empty");
        }

        FinancialPlan financialPlan = new FinancialPlan();
        financialPlan.setPlanId(UUID.randomUUID().toString());
        financialPlan.setUserId(userId);
        financialPlan.setPlanName(financialPlanReq.getPlanName());
        financialPlan.setStartDate(financialPlanReq.getStartDate());
        financialPlan.setEndDate(financialPlanReq.getEndDate());
        financialPlan.setNotes(financialPlanReq.getNotes());
        financialPlan.setDeleted(false);
        FinancialPlan savedFinancialPlan = financialPlanRepository.save(financialPlan);
        return mapToFinancialPlanRes(savedFinancialPlan);
    }

    @Override
    public FinancialPlanRes updateFinancialPlan(String userId, String planId, FinancialPlanReq financialPlanReq) {
        FinancialPlan financialPlan = financialPlanRepository.findByPlanIdAndUserId(planId, userId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));
        financialPlan.setPlanName(financialPlanReq.getPlanName());
        financialPlan.setNotes(financialPlanReq.getNotes());
        FinancialPlan updateFinancialPlan = financialPlanRepository.save(financialPlan);
        return mapToFinancialPlanRes(updateFinancialPlan);
    }

    @Override
    public Map<String, Object> deleteFinancialPlan(String userId, String planId) {
        Map<String, Object> response = new HashMap<>();
        if (planId == null || planId.isEmpty() || userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("PlanId and UserId must not be null or empty");
        }

        FinancialPlan financialPlan = financialPlanRepository.findByPlanIdAndUserId(planId, userId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan not found with id: " + planId));

        try {

            List<SavingTargetRes> savingTargetList = savingTargetClientService.getFinancialSavingTargetByPlan(userId, planId);
            if (savingTargetList != null && !savingTargetList.isEmpty()) {
                if (!transactionClientService.deleteTransaction(userId, planId)) {
                    throw new ServiceUnavailableException("Failed to delete transaction for planId: " + planId);
                }
            }

            List<TransactionRes> transactionList = transactionClientService.getTransactionByPlan(userId, planId);
            if (transactionList != null && !transactionList.isEmpty()) {
                if (!savingTargetClientService.deleteSavingTarget(userId, planId)) {
                    throw new ServiceUnavailableException("Failed to delete saving target for planId: " + planId);
                }
            }

            financialPlan.setDeleted(true);
            financialPlanRepository.save(financialPlan);

        } catch (Exception e) {
            logger.error("Error during deleteFinancialPlan for planId: {}", planId, e);
            rollbackService.rollbackDeleteFinancialPlan(userId, financialPlan);
            throw e;
        }

        response.put("deletedPlanId", planId);
        response.put("info", "The Plan was removed from the database.");
        return response;
    }

    @Override
    public List<FinancialPlanRes> getFinancePlanByDate(DateReq dateReq) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date utilDateStart = null;
        Date utilDateEnd = null;
        try {
            utilDateStart = format.parse(dateReq.getDate_start());
            utilDateEnd = format.parse(dateReq.getDate_end());
        } catch (ParseException e) {
            // Handle parsing error
            e.printStackTrace();
        }





        List<FinancialPlan> financialPlanList = financialPlanRepository.getPlaneByDateStartAndDateEnd(utilDateStart, utilDateEnd);

        if(financialPlanList.isEmpty()){
            throw new DataNotFoundException("Data FinancialPlan Not Found");
        }
        return financialPlanList.stream()
                .map(this::mapToFinancialPlanRes).toList();

    }

    private FinancialPlanRes mapToFinancialPlanDetailRes(FinancialPlan financialPlan, List<SavingTargetRes> savingTargetRes, List<TransactionRes> transactionRes){
        FinancialPlanRes financialPlanRes = new FinancialPlanRes();
        financialPlanRes.setPlanId(financialPlan.getPlanId());
        financialPlanRes.setUserId(financialPlan.getUserId());
        financialPlanRes.setPlanName(financialPlan.getPlanName());
        financialPlanRes.setStartDate(financialPlan.getStartDate());
        financialPlanRes.setEndDate(financialPlan.getEndDate());
        financialPlanRes.setNotes(financialPlan.getNotes());
        financialPlanRes.setSavingTarget(savingTargetRes);
        financialPlanRes.setTransactionRes(transactionRes);
        return financialPlanRes;
    }

    private FinancialPlanRes mapToFinancialPlanRes(FinancialPlan financialPlan){
        FinancialPlanRes financialPlanRes = new FinancialPlanRes();
        financialPlanRes.setPlanId(financialPlan.getPlanId());
        financialPlanRes.setUserId(financialPlan.getUserId());
        financialPlanRes.setPlanName(financialPlan.getPlanName());
        financialPlanRes.setStartDate(financialPlan.getStartDate());
        financialPlanRes.setEndDate(financialPlan.getEndDate());
        financialPlanRes.setNotes(financialPlan.getNotes());
        return financialPlanRes;
    }



}
