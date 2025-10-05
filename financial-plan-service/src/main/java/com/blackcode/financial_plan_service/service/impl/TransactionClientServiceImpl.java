package com.blackcode.financial_plan_service.service.impl;

import com.blackcode.financial_plan_service.dto.MessageRes;
import com.blackcode.financial_plan_service.dto.RollbackRes;
import com.blackcode.financial_plan_service.dto.SavingTargetRes;
import com.blackcode.financial_plan_service.dto.TransactionRes;
import com.blackcode.financial_plan_service.exceptions.DataNotFoundException;
import com.blackcode.financial_plan_service.exceptions.ExternalServiceException;
import com.blackcode.financial_plan_service.helper.TypeRefs;
import com.blackcode.financial_plan_service.service.TransactionClientService;
import com.blackcode.financial_plan_service.utils.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionClientServiceImpl implements TransactionClientService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionClientServiceImpl.class);

    private static final String TRANSACTION_API_PATH = "/api/transaction";

    private static final String USER_ID_HEADER = "X-User-Id";

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final String INTERNAL_TOKEN_VALUE = "secret-key-123";

    private final WebClient transactionWebClient;

    public TransactionClientServiceImpl(@Qualifier("transactionClient") WebClient transactionWebClient) {
        this.transactionWebClient = transactionWebClient;
    }

    @Override
    @Retry(name = "transactionalService")
    @CircuitBreaker(name = "transactionalService", fallbackMethod = "fallback")
    public List<TransactionRes> getTransactionByPlan(String userId, String planId) {
        if (userId == null || planId == null || planId.isEmpty()) {
            return Collections.emptyList();
        }

        String uri = TRANSACTION_API_PATH + "/getTransactionByPlan/" + planId;

        ParameterizedTypeReference<ApiResponse<List<TransactionRes>>> typeRef = TypeRefs.transactionDtoResponse();

        ApiResponse<List<TransactionRes>> response = transactionWebClient.get()
                .uri(uri)
                .header("X-User-Id", userId)
                .header("X-Internal-Token", "secret-key-123")
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    logger.info("Response status: {}", status);

                    if (status.is4xxClientError() || status.is5xxServerError()) {
                        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.warn("Error body: {}", errorBody);
                            if (status.value() == 404 || status.value() == 204) {
                                return Mono.just(ApiResponse.success("No data found", status.value(), Collections.<TransactionRes>emptyList()));
                            }
                            return Mono.error(new RuntimeException("Error from transactional-service"));
                        });
                    }

                    return clientResponse.bodyToMono(typeRef)
                            .flatMap(apiRes -> {
                                if (apiRes == null || apiRes.getData() == null || apiRes.getData().isEmpty()) {
                                    logger.warn("Received empty data in 200 OK response");
                                    return Mono.just(ApiResponse.success("No data found", 200, Collections.<TransactionRes>emptyList()));
                                }
                                return Mono.just(apiRes);
                            });
                })
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> {
                    logger.error("Fallback error: {}", e.getMessage());
                    return Mono.just(ApiResponse.error("Failed to fetch data", 500));
                })
                .block();

        if (response == null || response.getData() == null) {
            logger.warn("No response or null data for planId: {}", planId);
            return Collections.emptyList();
        }

        return response.getData();
    }

    public List<TransactionRes> fallback(String userId, String planId, Throwable throwable) {
        logger.error("Gagal ambil data planId untuk ID {}. Error: {}", planId, throwable.toString());
        return null;
    }

    @Override
    @Retry(name = "transactionalService")
    @CircuitBreaker(name = "transactionalService", fallbackMethod = "deleteFallback")
    public boolean deleteTransaction(String userId, String planId) {
        if (planId == null || planId.isEmpty()) {
            throw new IllegalArgumentException("Plan ID tidak boleh kosong");
        }

        String uri = TRANSACTION_API_PATH +"/deleteTransactionByPlan/"+ planId;

        ParameterizedTypeReference<ApiResponse<MessageRes>> typeRef = TypeRefs.deleteResponse();

        try {
            transactionWebClient.delete()
                    .uri(uri)
                    .header(USER_ID_HEADER, userId)
                    .header(INTERNAL_TOKEN_HEADER, INTERNAL_TOKEN_VALUE)
                    .exchangeToMono(clientResponse -> {
                        HttpStatusCode status = clientResponse.statusCode();
                        logger.info("Delete Response status: {}", status);

                        if (status.isError()) {
                            return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                logger.error("Error deleting saving target: {}", errorBody);
                                return Mono.error(new ExternalServiceException("Error deleting saving target: " + errorBody));
                            });
                        }
//                        return Mono.just(true);
                        return clientResponse.bodyToMono(typeRef);
                    })
                    .timeout(Duration.ofSeconds(3))
                    .block();
            return true;
        } catch (Exception e) {
            logger.error("Error saat menghapus saving target: {}", e.getMessage());
            throw new ExternalServiceException("Gagal menghubungi financial-saving-target-service", e);
        }
    }

    public boolean deleteFallback(String userId, String planId, Throwable throwable) {
        logger.error("Fallback triggered saat delete financial-saving-target. PlanId: {}, error: {}", planId, throwable.toString());
        return false;
    }

    @Override
    @Retry(name = "transactionalService")
    @CircuitBreaker(name = "transactionalService", fallbackMethod = "rollbackFallback")
    public RollbackRes rollbackTransaction(String userId, String planId) {
        if (planId == null || planId.isEmpty()) {
            throw new IllegalArgumentException("Plan ID tidak boleh kosong");
        }

        String uri = TRANSACTION_API_PATH + "/rollbackTransactionByPlan/" + planId;

        try {
            RollbackRes result = transactionWebClient.post()
                    .uri(uri)
                    .header(USER_ID_HEADER, userId)
                    .header(INTERNAL_TOKEN_HEADER, INTERNAL_TOKEN_VALUE)
                    .retrieve()
                    .bodyToMono(RollbackRes.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();

            if (result == null) {
                return new RollbackRes(false, "DELETE", "Rollback transaction gagal: response kosong untuk planId: " + planId);
            }

            return result;

        } catch (Exception e) {
            logger.error("Gagal rollback transaction untuk planId: {}", planId, e);
            throw new ExternalServiceException("Gagal menghubungi endpoint rollback transaction", e);
        }
    }

    public RollbackRes rollbackFallback(String userId, String planId, Throwable throwable) {
        logger.error("Fallback triggered saat rollbackFallback financial-saving-target. PlanId: {}, error: {}", planId, throwable.toString());
        return new RollbackRes(false, "DELETE", "Fallback rollback transaction karena: " + throwable.getMessage());
    }
}
