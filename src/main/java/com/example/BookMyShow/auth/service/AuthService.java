package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.*;

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
