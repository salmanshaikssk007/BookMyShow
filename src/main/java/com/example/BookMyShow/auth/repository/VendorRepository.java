package com.example.BookMyShow.auth.repository;

import com.example.BookMyShow.auth.entity.VendorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository interface for VendorProfile entity
// It extends JpaRepository to provide CRUD operations and custom query methods
// The VendorProfile entity is identified by a Long type ID
public interface VendorRepository extends JpaRepository<VendorProfile, Long> {

    // Method to find a vendor by their username
    Optional<VendorProfile> findByBussinessName(String bussinessName);

    // Method to find a vendor by their email
    Optional<VendorProfile> findByEmail(String email);

    // Method to check if a vendor exists by their email
    Boolean existsByEmail(String email);

    // Method to check if a vendor exists by their username
    Boolean existsByBussinessName(String bussinessName);
}
