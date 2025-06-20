package com.example.BookMyShow.auth.dto;

import com.example.BookMyShow.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/**
 * JWTResponse is a Data Transfer Object (DTO) used to encapsulate the response
 * containing the JWT token and user details after successful authentication.
 * It includes fields for the access token, token type, username, and roles.
 */
public class JWTResponse {
    private String accessToken; // The JWT access token issued to the user
    private String type = "Bearer";
    private Long id; // Unique identifier for the user
    private String username; // Username of the authenticated user
    private String email; // Email of the authenticated user
    private Role role; // Role of the authenticated user, e.g., USER, ADMIN, VENDOR
}
