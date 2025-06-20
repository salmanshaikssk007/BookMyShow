package com.example.BookMyShow.auth.controller;

import com.example.BookMyShow.auth.dto.JWTResponse;
import com.example.BookMyShow.auth.dto.LoginRequest;
import com.example.BookMyShow.auth.dto.SignUpRequest;
import com.example.BookMyShow.auth.service.AuthService;
import jakarta.annotation.PostConstruct;
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
    // /auth/signup to register a new user
    @PostMapping("/signup")
    public ResponseEntity<String> register(@Valid @RequestBody  SignUpRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
    //  /auth/login to login a user
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest request) {
        JWTResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    // This method is called after the bean is initialized by Spring
    @PostConstruct
    public void init() {
        System.out.println("✅ AuthController is loaded by Spring!");
    }
}
