package com.blackcode.transaction_service.repository;

import com.blackcode.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query(value = "SELECT * FROM tb_transaction WHERE deleted = false AND user_id = :userId AND plan_id = :planId " , nativeQuery = true)
    List<Transaction> findByUserIdAndPlanId(@Param("userId") String userId, @Param("planId") String planId);


    @Query(value = "SELECT * FROM tb_transaction WHERE deleted = false AND user_id = :userId AND plan_id = :planId AND transaction_id = :transactionId " , nativeQuery = true)
    Optional<Transaction> findByUserIdAndPlanIdAndTransactionId(@Param("userId") String userId, @Param("planId") String planId, @Param("transactionId") String transactionId);

    @Query(value = "SELECT * FROM tb_transaction WHERE user_id = :userId AND plan_id = :planId AND deleted = true AND delete_reason = :deleteReason", nativeQuery = true)
    List<Transaction> findByUserIdAndPlanIdAndDeletedTrueAndDeleteReason(@Param("userId") String userId, @Param("planId") String planId, @Param("deleteReason") String deleteReason);

}
