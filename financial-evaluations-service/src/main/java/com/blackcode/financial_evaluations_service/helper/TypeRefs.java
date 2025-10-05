package com.blackcode.financial_evaluations_service.helper;


import com.blackcode.financial_evaluations_service.dto.*;
import com.blackcode.financial_evaluations_service.utils.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public class TypeRefs {



    public static ParameterizedTypeReference<ApiResponse<FinancialPlanRes>> financialPlanDtoResponse() {
        return new ParameterizedTypeReference<>() {};
    }

    public static ParameterizedTypeReference<ApiResponse<List<SavingTargetRes>>> savingTargetDtoResponse() {
        return new ParameterizedTypeReference<>() {};
    }

    public static ParameterizedTypeReference<ApiResponse<List<TransactionRes>>> transactionDtoResponse() {
        return new ParameterizedTypeReference<>() {};
    }



}
