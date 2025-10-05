package com.blackcode.financial_saving_targets_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RollbackRes {

    private boolean status;

    private String operationType;

    private String message;

}
