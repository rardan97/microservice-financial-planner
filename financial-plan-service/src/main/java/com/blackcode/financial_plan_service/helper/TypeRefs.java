package com.blackcode.financial_plan_service.helper;

import com.blackcode.financial_plan_service.dto.MessageRes;
import com.blackcode.financial_plan_service.dto.RollbackRes;
import com.blackcode.financial_plan_service.dto.SavingTargetRes;
import com.blackcode.financial_plan_service.dto.TransactionRes;
import com.blackcode.financial_plan_service.utils.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public class TypeRefs {

    public static ParameterizedTypeReference<ApiResponse<List<SavingTargetRes>>> savingTargetDtoResponse() {
        return new ParameterizedTypeReference<>() {};
    }

    public static ParameterizedTypeReference<ApiResponse<List<TransactionRes>>> transactionDtoResponse() {
        return new ParameterizedTypeReference<>() {};
    }

    public static ParameterizedTypeReference<ApiResponse<MessageRes>> deleteResponse() {
        return new ParameterizedTypeReference<>() {};
    }

    public static ParameterizedTypeReference<ApiResponse<RollbackRes>> rollbackResponse() {
        return new ParameterizedTypeReference<>() {};
    }

}
