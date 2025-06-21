package com.example.BookMyShow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreationRequest {

    @NotBlank(message = "Admin name cannot be black")
    private String adminName;

    @NotBlank(message = "Admin email cannot be blank")
    private String email;

    @NotBlank(message = "Admin password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Admin phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "Admin zone code cannot be blank")
    private String zoneCode; // Zone code of the admin, e.g., "US", "us-ca"


}
