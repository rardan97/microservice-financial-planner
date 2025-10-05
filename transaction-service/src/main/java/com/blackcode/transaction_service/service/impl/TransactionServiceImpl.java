package com.blackcode.transaction_service.service.impl;

import com.blackcode.transaction_service.dto.*;
import com.blackcode.transaction_service.exceptions.DataNotFoundException;
import com.blackcode.transaction_service.model.Category;
import com.blackcode.transaction_service.model.PaymentType;
import com.blackcode.transaction_service.model.Transaction;
import com.blackcode.transaction_service.repository.CategoryRepository;
import com.blackcode.transaction_service.repository.TransactionRepository;
import com.blackcode.transaction_service.service.CategoryService;
import com.blackcode.transaction_service.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<TransactionRes> getTransactionByPlan(String userId, String planId) {
        List<Transaction> transaction = transactionRepository.findByUserIdAndPlanId(userId, planId);

        if (transaction == null) {
            return Collections.emptyList();
        }

        return transaction.stream()
                .map(this::mapToTransactionRes).toList();
    }

    @Override
    public TransactionRes getTransactionAllById(String userId, String planId, String transactionId) {
        Transaction transaction = transactionRepository.findByUserIdAndPlanIdAndTransactionId(userId, planId, transactionId)
                .orElseThrow(() -> new DataNotFoundException("financialSavingTarget Not Found with id : "+planId));
        return mapToTransactionRes(transaction);
    }

    @Override
    public TransactionRes createTransaction(String userId, String planId, TransactionReq transactionReq) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setPlanId(transactionReq.getPlanId());
        transaction.setUserId(userId);
        transaction.setTransactionDate(transactionReq.getTransactionDate());
        transaction.setTransactionName(transactionReq.getTransactionName());

        Category categoryEntity = categoryRepository.findById(transactionReq.getCategories())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + transactionReq.getCategories()));

        transaction.setCategory(categoryEntity);

        PaymentType paymentTypeEnum = parseEnum(PaymentType.class, transactionReq.getPaymentType());
        transaction.setPaymentType(paymentTypeEnum);
        transaction.setAmount(transactionReq.getAmount());
        transaction.setDescription(transactionReq.getDescription());
        transaction.setDeleted(false);
        transaction.setDeleteReason(null);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionRes(savedTransaction);
    }

    @Override
    public TransactionRes updateTransaction(String userId, String planId, String transactionId, TransactionReq transactionReq) {
        Transaction transaction = transactionRepository.findByUserIdAndPlanIdAndTransactionId(userId, planId, transactionId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));

        transaction.setTransactionName(transactionReq.getTransactionName());
        Category categoryEntity = categoryRepository.findById(transactionReq.getCategories())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + transactionReq.getCategories()));

        transaction.setCategory(categoryEntity);

        PaymentType paymentTypeEnum = parseEnum(PaymentType.class, transactionReq.getPaymentType());
        transaction.setPaymentType(paymentTypeEnum);
        transaction.setAmount(transactionReq.getAmount());
        transaction.setDescription(transactionReq.getDescription());
        Transaction updateFinancialSavingTarget = transactionRepository.save(transaction);
        return mapToTransactionRes(updateFinancialSavingTarget);
    }

    @Override
    public MessageRes deleteTransactionByPlan(String userId, String planId) {
        if (planId == null || planId.isEmpty() || userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("PlanId and UserId must not be null or empty");
        }
        MessageRes responseData = new MessageRes();
        List<Transaction> transactions = transactionRepository.findByUserIdAndPlanId(userId, planId);
        if (transactions == null || transactions.isEmpty()) {
            responseData.setMessage("No transactions found for this plan.");
            return responseData;
        }
        for (Transaction tx : transactions) {
            tx.setDeleted(true);
            tx.setDeleteReason("BY_PLAN");
        }
        transactionRepository.saveAll(transactions);
        responseData.setStatus(true);
        responseData.setMessage("Transactions related to the plan were deleted.");
        return responseData;
    }

    @Override
    public MessageRes deleteTransactionById(String userId, String planId, String transactionId) {
        if (planId == null || planId.isEmpty() || userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("PlanId and UserId must not be null or empty");
        }

        Transaction transaction = transactionRepository.findByUserIdAndPlanIdAndTransactionId(userId, planId, transactionId)
                .orElseThrow(() -> new DataNotFoundException("FinancialPlan Not Found with id : "+planId));

        transaction.setDeleted(true);
        transaction.setDeleteReason("MANUAL");
        transactionRepository.save(transaction);
        MessageRes responseData = new MessageRes();
        responseData.setStatus(true);
        responseData.setMessage("Transactions was removed from the database.");
        return responseData;
    }

    @Override
    public RollbackRes rollbackTransactionByPlan(String userId, RollbackReq rollbackReq) {
        List<Transaction> deletedTransactions = transactionRepository
                .findByUserIdAndPlanIdAndDeletedTrueAndDeleteReason(userId, rollbackReq.getPlanId(), "BY_PLAN");

        if (deletedTransactions.isEmpty()) {
            return new RollbackRes(false, "ROLLBACK", "Tidak ada transaksi yang dihapus dengan planId: " + rollbackReq.getPlanId());
        }

        for (Transaction tx : deletedTransactions) {
            tx.setDeleted(false);
            tx.setDeleteReason(null);

        }

        transactionRepository.saveAll(deletedTransactions);

        return new RollbackRes(true, "ROLLBACK", "Rollback delete transaksi by plan berhasil untuk planId: " + rollbackReq.getPlanId());
    }

    private CategoryRes mapToCategoryRes(Category category) {
        if (category == null) return null;
        return new CategoryRes(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getCategoryType()
        );
    }

    private TransactionRes mapToTransactionRes(Transaction transaction){
        TransactionRes transactionRes = new TransactionRes();
        transactionRes.setTransactionId(transaction.getTransactionId());
        transactionRes.setUserId(transaction.getUserId());
        transactionRes.setPlanId(transaction.getPlanId());
        transactionRes.setTransactionDate(transaction.getTransactionDate());
        transactionRes.setCategories(mapToCategoryRes(transaction.getCategory()));
        transactionRes.setAmount(transaction.getAmount());
        transactionRes.setPaymentType(transaction.getPaymentType() != null ? transaction.getPaymentType().name() : null);
        transactionRes.setDescription(transaction.getDescription());
        return transactionRes;
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String validValues = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Invalid value '" + value + "'. Valid values: " + validValues);
        }
    }
}
