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
@Table(name = "login_user_cred_role_check") // Table name in the database
public class LoginUserCredRoleCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the login user credential role check

    @Column(nullable = false, unique = true)
    private String username; // Unique username for the user, must not be blank

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Role of the user, e.g., ROLE_USER, ROLE_ADMIN, ROLE_VENDOR

    @Column(nullable = false)
    private Long entity_id; // Unique identifier for the entity associated with the user, e.g., admin or vendor ID
}
