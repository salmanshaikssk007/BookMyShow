package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.AdminCreationRequest;
import com.example.BookMyShow.auth.entity.AdminProfile;
import com.example.BookMyShow.auth.entity.AdminZone;
import com.example.BookMyShow.auth.entity.LoginUserCredRoleCheck;
import com.example.BookMyShow.auth.entity.Role;
import com.example.BookMyShow.auth.repository.AdminRepository;
import com.example.BookMyShow.auth.repository.AdminZoneRepository;
import com.example.BookMyShow.auth.repository.LoginCredRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;
    private final AdminZoneRepository adminZoneRepository;
    private final LoginCredRepository loginCredRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Creates a new admin profile with the provided request details.
     *
     * @param request the admin creation request containing admin details
     */
    @Override
    @Transactional
    public void createAdmin(AdminCreationRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Get the currently authenticated admin
        String username = auth.getName();
        AdminProfile creator = adminRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        AdminZone creatorZone = creator.getAdminZone();
        AdminZone targetZone = adminZoneRepository
                .findByZoneCode(request.getZoneCode())
                .orElseThrow(() -> new RuntimeException("Target zone not found"));

        // Zone Hireachy Check
        int creatorZoneLevel = creatorZone.getLevel();
        int targetZoneLevel = targetZone.getLevel();

        boolean sameBranch = targetZone.getZoneCode().startsWith(creatorZone.getZoneCode());
        boolean allowedLevel = false ;
        if(creatorZoneLevel == 0){
            // root so can create to country level zone
            sameBranch = creatorZone.getZoneCode().equals("WORLD") || creatorZone.getZoneCode().equals("world");
            allowedLevel = targetZoneLevel == 1;
        }else{
            allowedLevel = targetZoneLevel == creatorZoneLevel + 1;
        }
        if (!sameBranch || !allowedLevel) {
            throw new RuntimeException("You are not allowed to create an admin in this zone");
        }

        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        if (loginCredRepository.existsByUsername(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create a new admin profile
        AdminProfile newAdmin = AdminProfile.builder()
                .adminName(request.getAdminName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .adminZone(targetZone)
                .createdByAdmin(creator)
                .build();

        // Save the new admin profile to the repository
        adminRepository.save(newAdmin);

        // Use email as the canonical login username for all admins (consistent with DevDataSeeder)
        LoginUserCredRoleCheck loginAdmin = LoginUserCredRoleCheck.builder()
                .username(request.getEmail())
                .role(Role.ROLE_ADMIN)
                .entity_id(newAdmin.getId())
                .build();
        // Save the login credentials with the role
        loginCredRepository.save(loginAdmin);
    }
}
