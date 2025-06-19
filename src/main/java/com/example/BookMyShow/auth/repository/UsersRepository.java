package com.example.BookMyShow.auth.repository;

import com.example.BookMyShow.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User , Long> {
    // Method to find a user by their username
    Optional<User> findByUsername(String username);

    // Method to find if a user exists by their email
    Boolean findByEmail(String email);

    // Method to check if a user exists by their username
    Boolean existsByUsername(String username);

}
