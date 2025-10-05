package com.blackcode.financial_evaluations_service.service.impl;

import com.blackcode.financial_evaluations_service.dto.FinancialPlanRes;
import com.blackcode.financial_evaluations_service.dto.SavingTargetRes;
import com.blackcode.financial_evaluations_service.helper.TypeRefs;
import com.blackcode.financial_evaluations_service.service.FinancialPlaneClientService;
import com.blackcode.financial_evaluations_service.utils.ApiResponse;
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

@Service
public class FinancialPlaneClientServiceImpl implements FinancialPlaneClientService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialPlaneClientServiceImpl.class);

    private static final String FINANCIAL_SAVING_TARGET_API_PATH = "/api/financial-plan";

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final String INTERNAL_TOKEN_VALUE = "secret-key-123";

    private final WebClient financialPlanWebClient;

    public FinancialPlaneClientServiceImpl(@Qualifier("financialPlanService") WebClient financialPlanWebClient) {
        this.financialPlanWebClient = financialPlanWebClient;
    }

    @Override
    @Retry(name = "financialPlanService")
    @CircuitBreaker(name = "financialPlanService", fallbackMethod = "fallback")
    public FinancialPlanRes getFinancialPlanById(String userId, String planId) {
        if (userId == null || planId == null || planId.isEmpty()) {
            return null;
        }

        String uri = FINANCIAL_SAVING_TARGET_API_PATH + "/getFinancialPlanById/" + planId;
        ParameterizedTypeReference<ApiResponse<FinancialPlanRes>> typeRef = TypeRefs.financialPlanDtoResponse();
        ApiResponse<FinancialPlanRes> response = financialPlanWebClient.get()
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
                                return Mono.just(ApiResponse.success("No data found", status.value(), null));
                            }
                            return Mono.error(new RuntimeException("Error from financial-plan-service"));
                        });
                    }

                    return clientResponse.bodyToMono(typeRef);
                })
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> {
                    logger.error("Fallback error: {}", e.getMessage());
                    return Mono.just(ApiResponse.error("Failed to fetch data", 500));
                })
                .block();

        if (response == null || response.getData() == null) {
            logger.warn("No response or null data for planId: {}", planId);
            return null;
        }

        return response.getData();
    }

    public FinancialPlanRes fallback(String userId, String planId, Throwable throwable) {
        logger.error("Fallback hit for planId {}. Error: {}", planId, throwable.toString());
        return null;
    }
}
