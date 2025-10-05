package com.blackcode.transaction_service.service.impl;

import com.blackcode.transaction_service.dto.CategoryReq;
import com.blackcode.transaction_service.dto.CategoryRes;
import com.blackcode.transaction_service.dto.MessageRes;
import com.blackcode.transaction_service.exceptions.DataNotFoundException;
import com.blackcode.transaction_service.model.Category;
import com.blackcode.transaction_service.repository.CategoryRepository;
import com.blackcode.transaction_service.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryRes> getCategoryAll(String userId) {
        List<Category> category = categoryRepository.findCategoryByUserId(userId);
        return category.stream()
                .map(this::mapToCategoryRes).toList();
    }

    @Override
    public CategoryRes getCategoryById(String userId, String categoryId) {
        Category category = categoryRepository.findCategoryByUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category Not Found with id : "+categoryId));
        return mapToCategoryRes(category);
    }

    @Override
    public CategoryRes createCategory(String userId, CategoryReq categoryReq) {
        Category category = new Category();
        category.setCategoryId(UUID.randomUUID().toString());
        category.setCategoryName(categoryReq.getCategoryName());
        category.setCategoryType(categoryReq.getCategoryType());
        category.setCategoryUserId(userId);
        return mapToCategoryRes(category);
    }

    @Override
    public CategoryRes updateCategory(String userId, String categoryId, CategoryReq categoryReq) {
        Category category = categoryRepository.findCategoryByUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category Not Found with id : "+categoryId));
        category.setCategoryName(categoryReq.getCategoryName());
        category.setCategoryType(categoryReq.getCategoryType());
        return mapToCategoryRes(category);
    }

    @Override
    public MessageRes deleteCategory(String userId, String categoryId) {
        return null;
    }

    private CategoryRes mapToCategoryRes(Category category){
        CategoryRes categoryRes = new CategoryRes();
        categoryRes.setCategoryId(category.getCategoryId());
        categoryRes.setCategoryName(category.getCategoryName());
        categoryRes.setCategoryType(category.getCategoryType());
        return categoryRes;
    }
}
