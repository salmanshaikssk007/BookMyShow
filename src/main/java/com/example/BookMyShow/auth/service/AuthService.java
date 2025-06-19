package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.JWTResponse;
import com.example.BookMyShow.auth.dto.LoginRequest;
import com.example.BookMyShow.auth.dto.SignUpRequest;

public interface AuthService {
    void register(SignUpRequest request);
    JWTResponse login(LoginRequest request);
}
