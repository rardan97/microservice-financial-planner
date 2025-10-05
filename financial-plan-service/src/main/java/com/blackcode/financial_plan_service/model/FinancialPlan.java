package com.blackcode.financial_plan_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_financial_plan")
public class FinancialPlan {

    @Id
    private String planId;

    private String userId;

    private String planName;

    private Date startDate;

    private Date endDate;

    private String notes;

    private boolean deleted = false;

}
