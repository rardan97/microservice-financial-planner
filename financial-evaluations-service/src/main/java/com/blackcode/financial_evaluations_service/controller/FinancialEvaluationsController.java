package com.blackcode.financial_evaluations_service.controller;

import com.blackcode.financial_evaluations_service.dto.FinancialEvaluationsRes;
import com.blackcode.financial_evaluations_service.service.FinancialEvaluationsService;
import com.blackcode.financial_evaluations_service.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial-evaluations")
public class FinancialEvaluationsController {

    private final FinancialEvaluationsService financialEvaluationsService;

    public FinancialEvaluationsController(FinancialEvaluationsService financialEvaluationsService) {
        this.financialEvaluationsService = financialEvaluationsService;
    }

    @GetMapping("/getEvaluationsAll")
    public ResponseEntity<ApiResponse<List<FinancialEvaluationsRes>>> getEvaluationsAll(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        List<FinancialEvaluationsRes> rtn = financialEvaluationsService.getEvaluationsAll(userId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, rtn));
    }

    @GetMapping("/getEvaluationsPlan/{planId}")
    public ResponseEntity<ApiResponse<FinancialEvaluationsRes>> getEvaluationsPlan(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialEvaluationsRes rtn = financialEvaluationsService.getTransactionByPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, rtn));
    }

    @PostMapping("/createEvaluationsPlan/{planId}")
    public ResponseEntity<ApiResponse<FinancialEvaluationsRes>> createTransaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialEvaluationsRes rtn = financialEvaluationsService.createEvaluationsPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("Success Create Transaction", 200, rtn));
    }
}
