package com.blackcode.financial_plan_service.repository;

import com.blackcode.financial_plan_service.model.FinancialPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialPlanRepository extends JpaRepository<FinancialPlan, String> {

    @Query(value = "SELECT * FROM tb_financial_plan WHERE deleted = false AND user_id = :userId", nativeQuery = true)
    List<FinancialPlan> findPlanByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM tb_financial_plan WHERE deleted = false AND plan_id = :planId AND user_id = :userId", nativeQuery = true)
    Optional<FinancialPlan> findByPlanIdAndUserId(@Param("planId") String planId, @Param("userId") String userId);

}
