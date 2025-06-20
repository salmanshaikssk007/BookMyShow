package com.example.BookMyShow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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

    @NotBlank(message = "Admin ID number cannot be blank")
    private String adminIdNo;

    @NotBlank(message = "Zone number cannot be blank")
    private Integer zoneNumber; // Zone number of the geo module, should be an integer

}
