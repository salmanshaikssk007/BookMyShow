package com.example.BookMyShow.auth.controller;

import com.example.BookMyShow.auth.dto.AdminCreationRequest;
import com.example.BookMyShow.auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestBody @Valid AdminCreationRequest request) {
        adminService.createAdmin(request);
        return ResponseEntity.ok("Admin created successfully");
    }
}
