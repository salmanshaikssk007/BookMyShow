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
@Table(name = "admin_profiles") // Table name in the database
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the admin profile

    @Column(nullable = false, unique = true)
    private String adminName; // Unique name for the admin

    @Column(nullable = false, unique = true)
    private String email; // Unique email for the admin

    @Column(nullable = false)
    private String password; // Password for the admin, should be stored securely (hashed)

    @Column(nullable = false)
    private String phoneNumber; // Contact number for the admin

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_ADMIN; // Role of the user, default is ROLE_ADMIN

    @Column(nullable = false)
    private boolean verified = false; // Verification status of the admin profile

    @Column(nullable = false , unique = true)
    private String adminIdNo; // Unique id no for the admin, if applicable

    @Column(nullable = false)
    private Integer zoneNumbeer; // Zone number of the geo module


}
