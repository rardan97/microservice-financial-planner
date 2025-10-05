package com.blackcode.financial_evaluations_service.repository;

import com.blackcode.financial_evaluations_service.model.FinancialEvaluations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialEvaluationsRepository extends JpaRepository<FinancialEvaluations, String> {

    @Query(value = "SELECT * FROM tb_financial_evaluations WHERE AND user_id = :userId AND plan_id = :planId" , nativeQuery = true)
    Optional<FinancialEvaluations> findByUserIdAndPlanId(@Param("userId") String userId, @Param("planId") String planId);

}
