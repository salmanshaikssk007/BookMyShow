package com.example.BookMyShow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users") // Table name in the database
// This class represents a User entity in the BookMyShow application.
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the user

    @Column(nullable = false, unique = true)
    private String username; // Unique username for the user

    @Column(nullable = false , unique = true)
    private String email; // Unique email for the user

    @Column(nullable = false)
    private String password; // Password for the user, should be stored securely (hashed)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
