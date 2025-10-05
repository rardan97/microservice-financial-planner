package com.blackcode.transaction_service.controller;

import com.blackcode.transaction_service.dto.*;
import com.blackcode.transaction_service.service.CategoryService;
import com.blackcode.transaction_service.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/getCategoryAll")
    public ResponseEntity<ApiResponse<List<CategoryRes>>> getCategoryAll(
            @RequestHeader("X-User-Id") String userId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        List<CategoryRes> rtn = categoryService.getCategoryAll(userId);
        return ResponseEntity.ok(ApiResponse.success("Category found",200, rtn));
    }

    @GetMapping("/getCategoryById/{categoryId}")
    public  ResponseEntity<ApiResponse<CategoryRes>> getCategoryById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("categoryId") String categoryId){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        CategoryRes rtn = categoryService.getCategoryById(userId, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category found",200, rtn));
    }

    @PostMapping("/createCategory")
    public ResponseEntity<ApiResponse<CategoryRes>> createCategory(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CategoryReq categoryReq) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        CategoryRes rtn = categoryService.createCategory(userId, categoryReq);
        return ResponseEntity.ok(ApiResponse.success("Success Create Category", 200, rtn));
    }

    @PutMapping("/updateCategory/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryRes>> updateCategory(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("categoryId") String categoryId,
            @RequestBody CategoryReq categoryReq){

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        CategoryRes rtn = categoryService.updateCategory(userId, categoryId, categoryReq);
        return ResponseEntity.ok(ApiResponse.success("Category Update Successfully", 200, rtn));
    }

    @DeleteMapping("/deleteCategory/{categoryId}")
    public ResponseEntity<ApiResponse<MessageRes>> deleteCategory(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("categoryId") String categoryId){
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        MessageRes rtn = categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", 200, rtn));
    }

}
