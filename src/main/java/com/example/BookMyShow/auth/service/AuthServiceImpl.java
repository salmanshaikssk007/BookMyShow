package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.*;
import com.example.BookMyShow.auth.entity.*;
import com.example.BookMyShow.auth.repository.AdminRepository;
import com.example.BookMyShow.auth.repository.LoginCredRepository;
import com.example.BookMyShow.auth.repository.UserRepository;
import com.example.BookMyShow.auth.repository.VendorRepository;
import com.example.BookMyShow.auth.util.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final LoginCredRepository loginCredRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public void registerUser(UserSignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        if (loginCredRepository.existsByUsername(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        LoginUserCredRoleCheck loginUser = LoginUserCredRoleCheck.builder()
                .username(request.getEmail())
                .role(Role.ROLE_USER)
                .entity_id(user.getId())
                .build();
        loginCredRepository.save(loginUser);
    }

    @Override
    @Transactional
    public void registerVendor(VendorSignUpRequest request) {
        if (vendorRepository.existsByBussinessName(request.getBussinessName())) {
            throw new RuntimeException("Business name is already taken");
        }
        if (vendorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        if (loginCredRepository.existsByUsername(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        VendorProfile vendor = VendorProfile.builder()
                .bussinessName(request.getBussinessName())
                .email(request.getEmail())
                .bussinessLicenseNumber(request.getBussinessLicenseNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .build();
        vendorRepository.save(vendor);

        LoginUserCredRoleCheck loginUser = LoginUserCredRoleCheck.builder()
                .username(request.getEmail())
                .role(Role.ROLE_VENDOR)
                .entity_id(vendor.getId())
                .build();
        loginCredRepository.save(loginUser);
    }

    @Override
    public JWTResponse login(LoginRequest request) {
        String username = request.getUsername();
        loginAttemptService.checkBlocked(username);

        try {
            LoginUserCredRoleCheck loginRecord = loginCredRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));

            Role role = loginRecord.getRole();
            Long entityId = loginRecord.getEntity_id();

            JWTResponse response;
            switch (role) {
                case ROLE_USER:
                    User user = userRepository.findById(entityId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new RuntimeException("Invalid username or password");
                    }
                    response = buildResponse(user.getEmail(), role, user.getId(), user.getUsername(), user.getEmail());
                    break;

                case ROLE_VENDOR:
                    VendorProfile vendor = vendorRepository.findById(entityId)
                            .orElseThrow(() -> new RuntimeException("Vendor not found"));
                    if (!passwordEncoder.matches(request.getPassword(), vendor.getPassword())) {
                        throw new RuntimeException("Invalid username or password");
                    }
                    response = buildResponse(vendor.getEmail(), role, vendor.getId(), vendor.getBussinessName(), vendor.getEmail());
                    break;

                case ROLE_ADMIN:
                    AdminProfile admin = adminRepository.findById(entityId)
                            .orElseThrow(() -> new RuntimeException("Admin not found"));
                    if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                        throw new RuntimeException("Invalid username or password");
                    }
                    response = buildResponse(admin.getEmail(), role, admin.getId(), admin.getAdminName(), admin.getEmail());
                    break;

                default:
                    throw new RuntimeException("Invalid role");
            }

            loginAttemptService.resetAttempts(username);
            return response;

        } catch (RuntimeException e) {
            // Don't count a lockout exception as a new failure
            if (!e.getMessage().startsWith("Account temporarily locked")) {
                loginAttemptService.recordFailure(username);
            }
            throw e;
        }
    }

    @Override
    public JWTResponse refresh(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        if (!"refresh".equals(jwtUtils.getTokenType(refreshToken))) {
            throw new RuntimeException("Invalid token type");
        }
        String jti = jwtUtils.getJtiFromToken(refreshToken);
        if (tokenBlacklistService.isBlacklisted(jti)) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        String username = jwtUtils.getUsernameFromJwtToken(refreshToken);
        Role role = Role.valueOf(jwtUtils.getRolesFromJwtToken(refreshToken));

        // Blacklist the used refresh token (rotation)
        long ttlMs = jwtUtils.getRemainingValidityMs(refreshToken);
        tokenBlacklistService.blacklist(jti, ttlMs);

        // Look up entity details for the response
        LoginUserCredRoleCheck loginRecord = loginCredRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long entityId = loginRecord.getEntity_id();

        switch (role) {
            case ROLE_USER:
                User user = userRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return buildResponse(username, role, user.getId(), user.getUsername(), user.getEmail());

            case ROLE_VENDOR:
                VendorProfile vendor = vendorRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Vendor not found"));
                return buildResponse(username, role, vendor.getId(), vendor.getBussinessName(), vendor.getEmail());

            case ROLE_ADMIN:
                AdminProfile admin = adminRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Admin not found"));
                return buildResponse(username, role, admin.getId(), admin.getAdminName(), admin.getEmail());

            default:
                throw new RuntimeException("Invalid role");
        }
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && jwtUtils.validateJwtToken(accessToken)) {
            String jti = jwtUtils.getJtiFromToken(accessToken);
            long ttlMs = jwtUtils.getRemainingValidityMs(accessToken);
            tokenBlacklistService.blacklist(jti, ttlMs);
        }
        if (refreshToken != null && jwtUtils.validateJwtToken(refreshToken)) {
            String jti = jwtUtils.getJtiFromToken(refreshToken);
            long ttlMs = jwtUtils.getRemainingValidityMs(refreshToken);
            tokenBlacklistService.blacklist(jti, ttlMs);
        }
    }

    private JWTResponse buildResponse(String subject, Role role, Long id, String username, String email) {
        String accessToken = jwtUtils.generateAccessToken(subject, role);
        String refreshToken = jwtUtils.generateRefreshToken(subject, role);
        return new JWTResponse(accessToken, refreshToken, "Bearer", id, username, email, role);
    }
}
