package com.blackcode.transaction_service.service;

import com.blackcode.transaction_service.dto.*;

import java.util.List;

public interface CategoryService {

    List<CategoryRes> getCategoryAll(String userId);

    CategoryRes getCategoryById(String userId, String categoryId);

    CategoryRes createCategory(String userId, CategoryReq categoryReq);

    CategoryRes updateCategory(String userId, String categoryId, CategoryReq categoryReq);

    MessageRes deleteCategory(String userId, String categoryId);

}
