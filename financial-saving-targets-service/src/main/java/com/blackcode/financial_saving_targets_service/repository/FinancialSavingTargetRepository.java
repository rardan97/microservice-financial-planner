package com.blackcode.financial_saving_targets_service.repository;

import com.blackcode.financial_saving_targets_service.model.FinancialSavingTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialSavingTargetRepository extends JpaRepository<FinancialSavingTarget, String> {

    @Query(value = "SELECT * FROM tb_transaction WHERE deleted = false AND user_id = :userId AND plan_id = :planId AND target_id = :targetId " , nativeQuery = true)
    Optional<FinancialSavingTarget> findByUserIdAndPlanIdAndTargetId(@Param("userId") String userId, @Param("planId") String planId, @Param("targetId") String targetId);

    @Query(value = "SELECT * FROM tb_financial_saving_target WHERE user_id = :userId AND plan_id = :planId AND target_id = :targetId AND deleted = true", nativeQuery = true)
    Optional<FinancialSavingTarget> findByUserIdAndPlanIdAndTargetIdDeletedTrue(@Param("userId") String userId, @Param("planId") String planId, @Param("targetId") String targetId);

    @Query(value = "SELECT * FROM tb_financial_saving_target WHERE user_id = :userId AND plan_id = :planId AND deleted = false", nativeQuery = true)
    List<FinancialSavingTarget> findByUserIdAndPlanId(@Param("userId") String userId, @Param("planId") String planId);

    @Query(value = "SELECT * FROM tb_financial_saving_target WHERE user_id = :userId AND plan_id = :planId AND deleted = true AND delete_reason = :deleteReason", nativeQuery = true)
    List<FinancialSavingTarget> findByUserIdAndPlanIdAndDeletedTrueAndDeleteReason(@Param("userId") String userId, @Param("planId") String planId, @Param("deleteReason") String deleteReason);

}
