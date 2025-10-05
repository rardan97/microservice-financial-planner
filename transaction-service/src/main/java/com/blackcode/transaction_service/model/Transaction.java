package com.blackcode.transaction_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_transaction")
public class Transaction {

    @Id
    private String transactionId;

    private String userId;

    private String planId;

    private String transactionName;

    private Date transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String description;

    private boolean deleted = false;

    @Column(name = "delete_reason")
    private String deleteReason;

}
