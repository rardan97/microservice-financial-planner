package com.blackcode.auth_service.repository;

import com.blackcode.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

}
