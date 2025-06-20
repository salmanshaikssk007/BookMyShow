package com.example.BookMyShow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vendor_profiles") // Table name in the database
public class VendorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the vendor profile

    @Column(nullable = false, unique = true)
    private String bussinessName; // Unique name for the vendor's business

    @Column(nullable = false, unique = true)
    private String email; // Unique email for the vendor

    @Column(nullable = false)
    private String password; // Password for the vendor, should be stored securely (hashed)

    @Column(nullable = false)
    private String phoneNumber; // Contact number for the vendor

    @Column(nullable = false)
    private String bussinessLicenseNumber; // Unique business identification number

    @Column(nullable = false)
    private boolean verified = false; // Verification status of the vendor profile

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_VENDOR; // Role of the user, default is ROLE_USER


}
