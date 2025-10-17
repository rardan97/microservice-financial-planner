package com.blackcode.financial_plan_service.service;


import com.blackcode.financial_plan_service.dto.DateReq;
import com.blackcode.financial_plan_service.dto.FinancialPlanReq;
import com.blackcode.financial_plan_service.dto.FinancialPlanRes;

import java.util.List;
import java.util.Map;

public interface FinancialPlanService {

    List<FinancialPlanRes> getFinancialPlanAll(String userId);

    FinancialPlanRes getFinancialPlanById(String userId, String planId);

    FinancialPlanRes createFinancialPlan(String userId, FinancialPlanReq financialPlanReq);

    FinancialPlanRes updateFinancialPlan(String userId, String planId, FinancialPlanReq financialPlanReq);

    Map<String, Object> deleteFinancialPlan(String userId, String planId);


    List<FinancialPlanRes> getFinancePlanByDate(DateReq dateReq);

}