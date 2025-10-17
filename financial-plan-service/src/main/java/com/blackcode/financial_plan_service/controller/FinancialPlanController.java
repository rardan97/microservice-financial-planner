package com.blackcode.financial_plan_service.controller;

import com.blackcode.financial_plan_service.dto.DateReq;
import com.blackcode.financial_plan_service.dto.FinancialPlanReq;
import com.blackcode.financial_plan_service.dto.FinancialPlanRes;
import com.blackcode.financial_plan_service.service.FinancialPlanService;
import com.blackcode.financial_plan_service.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/financial-plan")
public class FinancialPlanController {

    private final FinancialPlanService financialPlanService;

    public FinancialPlanController(FinancialPlanService financialPlanService) {
        this.financialPlanService = financialPlanService;
    }

    @GetMapping("/getFinancialPlanAll")
    public  ResponseEntity<ApiResponse<List<FinancialPlanRes>>> getFinancialPlanAll(
            @RequestHeader("X-User-Id") String userId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        List<FinancialPlanRes> financialPlanRes = financialPlanService.getFinancialPlanAll(userId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, financialPlanRes));
    }

    @PostMapping("/getFinancePlanByDate")
    public ResponseEntity<ApiResponse<List<FinancialPlanRes>>> getFinancePlanByDate(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody DateReq dateReq){
        List<FinancialPlanRes> financialPlanRes = financialPlanService.getFinancePlanByDate(dateReq);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found Bas On Date : Success", 200, financialPlanRes));
    }

    @GetMapping("/getFinancialPlanById/{planId}")
    public  ResponseEntity<ApiResponse<FinancialPlanRes>> getFinancialPlanById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }

        FinancialPlanRes financialPlanRes = financialPlanService.getFinancialPlanById(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, financialPlanRes));
    }

    @PostMapping("/createFinancialPlan")
    public ResponseEntity<ApiResponse<FinancialPlanRes>> createFinancialPlane(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody FinancialPlanReq financialPlanReq) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialPlanRes messageRes = financialPlanService.createFinancialPlan(userId, financialPlanReq);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan Create Successfully", 200, messageRes));
    }

    @PutMapping("/updateFinancialPlan/{planId}")
    public ResponseEntity<ApiResponse<FinancialPlanRes>> updateFinancialPlane(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @RequestBody FinancialPlanReq financialPlanReq){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialPlanRes financialPlanRes = financialPlanService.updateFinancialPlan(userId, planId, financialPlanReq);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan Update Successfully", 200, financialPlanRes));
    }

    @DeleteMapping("/deleteFinancialPlan/{planId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteFinancialPlane(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        Map<String, Object> rtn = financialPlanService.deleteFinancialPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan deleted successfully", 200, rtn));
    }
}