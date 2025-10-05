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

    @GetMapping("/getEvaluationsPlan/{planId}")
    public ResponseEntity<ApiResponse<FinancialEvaluationsRes>> getEvaluationsPlan(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialEvaluationsRes rtn = financialEvaluationsService.getEvaluationsPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("getEvaluationsPlan found",200, rtn));
    }

    @PostMapping("/createEvaluationsPlan/{planId}")
    public ResponseEntity<ApiResponse<FinancialEvaluationsRes>> createTransaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialEvaluationsRes rtn = financialEvaluationsService.createEvaluationsPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("Success Create EvaluationsPlan", 200, rtn));
    }
}
