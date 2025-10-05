package com.blackcode.auth_service.model;

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
@Table(name = "tb_user")
public class User {

    @Id
    private String userId;

    private String fullName;

    private String email;

    private String username;

    private String password;

    @Column(nullable = false)
    private boolean deleted = false;

    public User(String fullName, String email, String username, String password) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
