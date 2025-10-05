package com.blackcode.financial_evaluations_service.service.impl;

import com.blackcode.financial_evaluations_service.dto.MessageRes;
import com.blackcode.financial_evaluations_service.dto.RollbackRes;
import com.blackcode.financial_evaluations_service.dto.SavingTargetReq;
import com.blackcode.financial_evaluations_service.dto.SavingTargetRes;
import com.blackcode.financial_evaluations_service.exceptions.ExternalServiceException;
import com.blackcode.financial_evaluations_service.helper.TypeRefs;
import com.blackcode.financial_evaluations_service.service.SavingTargetClientService;
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
}
