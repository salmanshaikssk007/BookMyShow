package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.JWTResponse;
import com.example.BookMyShow.auth.dto.LoginRequest;
import com.example.BookMyShow.auth.dto.UserSignUpRequest;
import com.example.BookMyShow.auth.dto.VendorSignUpRequest;

public interface AuthService {
    /**
     * Registers a new user with the provided sign-up request.
     *
     * @param request the user sign-up request containing user details
     */
    void registerUser(UserSignUpRequest request);
    void registerVendor(VendorSignUpRequest request);
    JWTResponse login(LoginRequest request);
}
