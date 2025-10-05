package com.blackcode.transaction_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_transaction_category")
public class Category {

    @Id
    private String categoryId;

    private String categoryName;

    private String categoryType;

    private String categoryUserId;

}
