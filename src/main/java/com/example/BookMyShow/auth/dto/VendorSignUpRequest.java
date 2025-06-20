package com.example.BookMyShow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * VendorSignUpRequest is a Data Transfer Object (DTO) used for vendor registration requests.
 * It contains the necessary fields for creating a new vendor: business name, email, password,
 * phone number, and business license number. The fields are validated to ensure they meet
 * specific criteria.
 */
public class VendorSignUpRequest {
    @NotBlank
    private String bussinessName; // Business name of the vendor, must not be blank

    @NotBlank
    @Email
    private String email; // Email of the vendor, must be a valid email format and not blank

    @NotBlank
    private String password; // Password for the vendor, must not be blank

    @NotBlank
    private String phoneNumber; // Contact number for the vendor, must not be blank

    @NotBlank
    private String bussinessLicenseNumber; // Unique business license number for the vendor, must not be blank
}
