package com.blackcode.financial_evaluations_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_financial_evaluations")
public class FinancialEvaluations {

    @Id
    private String evaluationId;

    private String planId;

    private String userId;

    private String netBalance;

    private String targetAmount;

    private boolean isAchievable;

    private LocalDateTime evaluatedAt;

}
