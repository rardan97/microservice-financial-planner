package com.blackcode.financial_saving_targets_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_financial_saving_target")
public class FinancialSavingTarget {

    @Id
    private String targetId;

    private String planId;

    private String userId;

    private String name;

    private String amount;

    private String notes;

    private boolean deleted = false;

    @Column(name = "delete_reason")
    private String deleteReason;
}
