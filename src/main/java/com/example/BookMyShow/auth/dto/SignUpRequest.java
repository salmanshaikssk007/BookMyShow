package com.example.BookMyShow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
/**
 * SignUpRequest is a Data Transfer Object (DTO) used for user registration requests.
 * It contains the necessary fields for creating a new user: username, email, and password.
 * The fields are validated to ensure they meet specific criteria.
 */
public class SignUpRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    private String username; // Username for the user, must be between 3 and 20 characters long

    @NotBlank(message = "Email is required")
    @Email
    private String email; // Email for the user, must be a valid email format

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters long")
    private String password; // Password for the user, must be between 6 and 40 characters long
}


