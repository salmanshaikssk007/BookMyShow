package com.example.BookMyShow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * LoginRequest is a Data Transfer Object (DTO) used for user login requests.
 * It contains the necessary fields for authentication: username and password.
 * The fields are validated to ensure they are not blank.
 */
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username; // Username for the user, must not be blank

    @NotBlank(message = "Password is required")
    private String password; // Password for the user, must not be blank
}
