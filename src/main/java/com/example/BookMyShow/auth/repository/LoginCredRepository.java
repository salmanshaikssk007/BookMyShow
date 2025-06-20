package com.example.BookMyShow.auth.repository;

import com.example.BookMyShow.auth.entity.LoginUserCredRoleCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginCredRepository extends JpaRepository<LoginUserCredRoleCheck, Long> {
    // Method to find a user by their username
    Optional<LoginUserCredRoleCheck> findByUsername(String username);

    // Method to check if a user exists by their username
    Boolean existsByUsername(String username);
}
