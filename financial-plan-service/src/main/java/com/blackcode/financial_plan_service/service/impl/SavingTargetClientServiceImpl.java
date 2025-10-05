package com.blackcode.financial_plan_service.service.impl;

import com.blackcode.financial_plan_service.dto.*;
import com.blackcode.financial_plan_service.exceptions.DataNotFoundException;
import com.blackcode.financial_plan_service.exceptions.ExternalServiceException;
import com.blackcode.financial_plan_service.helper.TypeRefs;
import com.blackcode.financial_plan_service.service.SavingTargetClientService;
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
public class SavingTargetClientServiceImpl implements SavingTargetClientService {

    private static final Logger logger = LoggerFactory.getLogger(SavingTargetClientServiceImpl.class);

    private static final String FINANCIAL_SAVING_TARGET_API_PATH = "/api/financial-saving-targets";

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final String INTERNAL_TOKEN_VALUE = "secret-key-123";

    private final WebClient savingTargetWebClient;

    public SavingTargetClientServiceImpl(@Qualifier("savingTargetClient") WebClient savingTargetWebClient) {
        this.savingTargetWebClient = savingTargetWebClient;
    }

    @Override
    @Retry(name = "financialSavingTargetService")
    @CircuitBreaker(name = "financialSavingTargetService", fallbackMethod = "fallback")
    public List<SavingTargetRes> getFinancialSavingTargetByPlan(String userId, String planId) {
        if (userId == null || planId == null || planId.isEmpty()) {
            return Collections.emptyList();
        }

        String uri = FINANCIAL_SAVING_TARGET_API_PATH + "/getFinancialSavingTargetByPlan/" + planId;

        ParameterizedTypeReference<ApiResponse<List<SavingTargetRes>>> typeRef = TypeRefs.savingTargetDtoResponse();

        ApiResponse<List<SavingTargetRes>> response = savingTargetWebClient.get()
                .uri(uri)
                .header(USER_ID_HEADER, userId)
                .header(INTERNAL_TOKEN_HEADER, INTERNAL_TOKEN_VALUE)
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    logger.info("Response status: {}", status);

                    if (status.is4xxClientError() || status.is5xxServerError()) {
                        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.warn("Error body: {}", errorBody);
                            if (status.value() == 404 || status.value() == 204) {
                                return Mono.just(ApiResponse.success("No data found", status.value(), Collections.<SavingTargetRes>emptyList()));
                            }
                            return Mono.error(new RuntimeException("Error from financial-saving-target-service"));
                        });
                    }

                    return clientResponse.bodyToMono(typeRef)
                            .flatMap(apiRes -> {
                                if (apiRes == null || apiRes.getData() == null || apiRes.getData().isEmpty()) {
                                    logger.warn("Received empty data in 200 OK response");
                                    return Mono.just(ApiResponse.success("No data found", 200, Collections.<SavingTargetRes>emptyList()));
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

    public List<SavingTargetRes> fallback(String userId, String planId, Throwable throwable) {
        logger.error("Gagal ambil data planId untuk ID {}. Error: {}", planId, throwable.toString());
        return null;
    }

    @Override
    @Retry(name = "financialSavingTargetService")
    @CircuitBreaker(name = "financialSavingTargetService", fallbackMethod = "deleteFallback")
    public boolean deleteSavingTarget(String userId, String planId) {
        if (planId == null || planId.isEmpty()) {
            throw new IllegalArgumentException("Plan ID tidak boleh kosong");
        }

        String uri = FINANCIAL_SAVING_TARGET_API_PATH +"/deleteSavingTargetByPlan/"+ planId;
        ParameterizedTypeReference<ApiResponse<MessageRes>> typeRef = TypeRefs.deleteResponse();
        try {
            savingTargetWebClient.delete()
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
        logger.error("Fallback triggered saat deleteFallback. PlanId: {}, error: {}", planId, throwable.toString());
        return false;
    }

    @Override
    @Retry(name = "financialSavingTargetService")
    @CircuitBreaker(name = "financialSavingTargetService", fallbackMethod = "rollbackFallback")
    public RollbackRes rollbackSavingTarget(String userId, String planId) {
        if (planId == null) {
            throw new IllegalArgumentException("request not valid");
        }

        SavingTargetReq request = new SavingTargetReq();
        request.setPlanId(planId);

        String uri = FINANCIAL_SAVING_TARGET_API_PATH + "/rollbackTransactionByPlan";
        ParameterizedTypeReference<ApiResponse<RollbackRes>> typeRef = TypeRefs.rollbackResponse();

        ApiResponse<RollbackRes> response = savingTargetWebClient.post()
                .uri(uri)
                .header(USER_ID_HEADER, userId)
                .header(INTERNAL_TOKEN_HEADER, INTERNAL_TOKEN_VALUE)
                .bodyValue(request)
                .exchangeToMono(clientResponse -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    logger.info("Response status: {}", status);

                    if (status.isError()) {

                        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                            logger.error("Error response body: {}", errorBody);
                            return Mono.error(new ExternalServiceException("Error from financial-saving-target-service: "+errorBody));
                        });
                    }

                    return clientResponse.bodyToMono(typeRef);
                })
                .onErrorResume(e -> {
                    logger.error("Error creating Saving Target: {}", e.getMessage());
                    return Mono.error(new ExternalServiceException("Failed to call financial-saving-target-service", e));
                })
                .timeout(Duration.ofSeconds(3))
                .block();

        if (response == null) {
            logger.warn("No response for financial-saving-target-service");
            throw new ExternalServiceException("No response from financial-saving-target-service");
        }
        return response.getData();
    }

    public RollbackRes rollbackFallback(String userId, String planId, Throwable throwable) {
        logger.error("Fallback triggered saat rollbackFallback financial-saving-target. PlanId: {}, error: {}", planId, throwable.toString());
        return new RollbackRes(false, "DELETE", "Fallback rollback transaction karena: " + throwable.getMessage());
    }
}
