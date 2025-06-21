package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.AdminCreationRequest;

public interface AdminService {

    /**
     * Creates a new admin profile with the provided request details.
     *
     * @param request the admin creation request containing admin details
     */
    void createAdmin(AdminCreationRequest request);
}
