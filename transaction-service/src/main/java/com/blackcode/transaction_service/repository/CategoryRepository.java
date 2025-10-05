package com.blackcode.transaction_service.repository;

import com.blackcode.transaction_service.model.Category;
import com.blackcode.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query(value = "SELECT * FROM tb_transaction_category WHERE category_user_id = :userId" , nativeQuery = true)
    List<Category> findCategoryByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM tb_transaction_category WHERE category_user_id = :userId AND category_id = :categoryId " , nativeQuery = true)
    Optional<Category> findCategoryByUserIdAndCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId);

}
