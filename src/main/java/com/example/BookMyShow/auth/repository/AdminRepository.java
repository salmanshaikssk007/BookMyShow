package com.example.BookMyShow.auth.repository;

import com.example.BookMyShow.auth.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminProfile, Long> {
    // find admin by id
    Optional<AdminProfile> findById(long id);

    // find admin by username
    Optional<AdminProfile> findByAdminName(String adminName);
    // find admin by email
    Optional<AdminProfile> findByEmail(String email);
    // exists by username
    boolean existsByAdminName(String adminName);

    // exists by email
    boolean existsByEmail(String email);
}
