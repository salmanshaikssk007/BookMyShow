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


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_ADMIN; // Role of the user, default is ROLE_ADMIN

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="zone_id", nullable = false)
   private AdminZone adminZone ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by_admin_id")
    private AdminProfile createdByAdmin; // Admin who created this profile, can be null if created by system

}
