package com.blackcode.transaction_service.controller;

import com.blackcode.transaction_service.dto.*;
import com.blackcode.transaction_service.service.TransactionService;
import com.blackcode.transaction_service.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/getTransactionByPlan/{planId}")
    public  ResponseEntity<ApiResponse<List<TransactionRes>>> getTransactionByPlan(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        List<TransactionRes> rtn = transactionService.getTransactionByPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("FinancialPlan found",200, rtn));
    }

    @GetMapping("/getTransactionAllById/{planId}/{transactionId}")
    public  ResponseEntity<ApiResponse<TransactionRes>> getTransactionAllById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @PathVariable("transactionId") String transactionId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        TransactionRes rtn = transactionService.getTransactionAllById(userId, planId, transactionId);
        return ResponseEntity.ok(ApiResponse.success("Transaction found",200, rtn));
    }

    @PostMapping("/createTransaction/{planId}")
    public ResponseEntity<ApiResponse<TransactionRes>> createTransaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @RequestBody TransactionReq transactionReq) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        TransactionRes rtn = transactionService.createTransaction(userId, planId, transactionReq);
        return ResponseEntity.ok(ApiResponse.success("Success Create Transaction", 200, rtn));
    }

    @PutMapping("/updateTransaction/{planId}/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionRes>> updateTransaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @PathVariable("transactionId") String transactionId,
            @RequestBody TransactionReq transactionReq){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        TransactionRes rtn = transactionService.updateTransaction(userId, planId, transactionId, transactionReq);
        return ResponseEntity.ok(ApiResponse.success("Transaction Update Successfully", 200, rtn));
    }

    @DeleteMapping("/deleteTransactionByPlan/{planId}")
    public ResponseEntity<ApiResponse<MessageRes>> deleteTransactionByPlan(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId){
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        MessageRes rtn = transactionService.deleteTransactionByPlan(userId, planId);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", 200, rtn));
    }


    @DeleteMapping("/deleteTransactionById/{planId}/{transactionId}")
    public ResponseEntity<ApiResponse<MessageRes>> deleteTransaction(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("planId") String planId,
            @PathVariable("transactionId") String transactionId){
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        MessageRes rtn = transactionService.deleteTransactionById(userId, planId, transactionId);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", 200, rtn));
    }

    @PostMapping("/rollbackTransactionByPlan")
    public ResponseEntity<ApiResponse<RollbackRes>> rollbackTransactionByPlan(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody RollbackReq rollbackReq) {


        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        RollbackRes rtn = transactionService.rollbackTransactionByPlan(userId, rollbackReq);
        return ResponseEntity.ok(ApiResponse.success("Success Rollback Transaction", 200, rtn));
    }

}
