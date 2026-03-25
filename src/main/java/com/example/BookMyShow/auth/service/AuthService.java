package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.*;

public interface AuthService {
    void registerUser(UserSignUpRequest request);
    void registerVendor(VendorSignUpRequest request);
    JWTResponse login(LoginRequest request);
    JWTResponse refresh(String refreshToken);
    void logout(String accessToken, String refreshToken);
}
