package com.blackcode.financial_saving_targets_service.controller;

import com.blackcode.financial_saving_targets_service.dto.*;
import com.blackcode.financial_saving_targets_service.service.FinancialSavingTargetService;
import com.blackcode.financial_saving_targets_service.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/financial-saving-targets")
public class FinancialSavingTargetController {

    private final FinancialSavingTargetService financialSavingTargetService;

    public FinancialSavingTargetController(FinancialSavingTargetService financialSavingTargetService) {
        this.financialSavingTargetService = financialSavingTargetService;
    }


    @GetMapping("/getFinancialSavingTargetByPlan/{planId}")
    public  ResponseEntity<ApiResponse<List<FinancialSavingTargetRes>>> getFinancialSavingTargetByPlan(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        List<FinancialSavingTargetRes> financialPlanRes = financialSavingTargetService.getFinancialSavingTargetByPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, financialPlanRes));
    }


    @GetMapping("/getFinancialSavingTargetsById/{planId}/{targetId}")
    public  ResponseEntity<ApiResponse<FinancialSavingTargetRes>> getFinancialSavingTargetsById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @PathVariable("targetId") String targetId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialSavingTargetRes financialPlanRes = financialSavingTargetService.getFinancialSavingTargetsById(userId, planId, targetId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, financialPlanRes));
    }

    @PostMapping("/createSavingTarget/{planId}")
    public ResponseEntity<ApiResponse<FinancialSavingTargetRes>> crateFinancialSavingTarget(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @RequestBody FinancialSavingTargetReq financialSavingTargetReq) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialSavingTargetRes messageRes = financialSavingTargetService.crateFinancialSavingTarget(userId, planId, financialSavingTargetReq);
        return ResponseEntity.ok(ApiResponse.success("Success", 200, messageRes));
    }

    @PutMapping("/updateSavingTarget/{planId}/{targetId}")
    public ResponseEntity<ApiResponse<FinancialSavingTargetRes>> updateFinancialSavingTarget(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @PathVariable("targetId") String targetId,
            @RequestBody FinancialSavingTargetReq financialPlanReq){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        FinancialSavingTargetRes financialSavingTargetRes = financialSavingTargetService.updateFinancialSavingTarget(userId, planId, targetId, financialPlanReq);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan Update Successfully", 200, financialSavingTargetRes));
    }

    @DeleteMapping("/deleteSavingTargetByPlan/{planId}")
    public ResponseEntity<ApiResponse<MessageRes>> deleteSavingTargetByPlan(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        MessageRes rtn = financialSavingTargetService.deleteSavingTargetByPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", 200, rtn));
    }

    @DeleteMapping("/deleteSavingTargetById/{planId}/{targetId}")
    public ResponseEntity<ApiResponse<MessageRes>> deleteSavingTargetById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @PathVariable("targetId") String targetId){
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        MessageRes rtn = financialSavingTargetService.deleteSavingTargetById(userId, planId, targetId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", 200, rtn));
    }

    @PostMapping("/rollbackSavingTarget")
    public ResponseEntity<ApiResponse<RollbackRes>> rollbackSavingTarget(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody RollbackReq rollbackReq) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        RollbackRes messageRes = financialSavingTargetService.rollbackFinancialSavingTarget(userId, rollbackReq);
        return ResponseEntity.ok(ApiResponse.success("Success Rollback Transaction", 200, messageRes));
    }
}
