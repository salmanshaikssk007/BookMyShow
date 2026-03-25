package com.example.BookMyShow.auth.controller;

import com.example.BookMyShow.auth.dto.JWTResponse;
import com.example.BookMyShow.auth.dto.LoginRequest;
import com.example.BookMyShow.auth.dto.RefreshRequest;
import com.example.BookMyShow.auth.dto.UserSignUpRequest;
import com.example.BookMyShow.auth.dto.VendorSignUpRequest;
import com.example.BookMyShow.auth.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/user/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserSignUpRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/vendor/signup")
    public ResponseEntity<String> registerVendor(@Valid @RequestBody VendorSignUpRequest request) {
        authService.registerVendor(request);
        return ResponseEntity.ok("Vendor registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request,
                                         @RequestBody(required = false) RefreshRequest body) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7).trim() : null;
        String refreshToken = (body != null) ? body.getRefreshToken() : null;
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ AuthController is loaded by Spring!");
    }
}
