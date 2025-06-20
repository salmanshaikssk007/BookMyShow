package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.JWTResponse;
import com.example.BookMyShow.auth.dto.LoginRequest;
import com.example.BookMyShow.auth.dto.UserSignUpRequest;
import com.example.BookMyShow.auth.dto.VendorSignUpRequest;
import com.example.BookMyShow.auth.entity.LoginUserCredRoleCheck;
import com.example.BookMyShow.auth.entity.Role;
import com.example.BookMyShow.auth.entity.User;
import com.example.BookMyShow.auth.entity.VendorProfile;
import com.example.BookMyShow.auth.repository.LoginCredRepository;
import com.example.BookMyShow.auth.repository.UserRepository;
import com.example.BookMyShow.auth.repository.VendorRepository;
import com.example.BookMyShow.auth.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final LoginCredRepository loginCredRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void registerUser(UserSignUpRequest request) {
        // Check if the user already exists by username or email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        // Check if the email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create a new user entity and set its properties
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encode the password
                .build();
        // Save the new user to the repository
        userRepository.save(user);
        // create a new user in the LoginCredRepository
        LoginUserCredRoleCheck loginUser = LoginUserCredRoleCheck.builder()
                .username(request.getUsername())
                .role(Role.ROLE_USER) // Set the role to USER
                .entity_id(user.getId()) // Set the entity ID to the user's ID
                .build();

        // Save the login credentials with the role
        loginCredRepository.save(loginUser);
    }

    @Override
    public void registerVendor(VendorSignUpRequest request) {
        // Check if the vendor already exists by username
        if(vendorRepository.existsByBussinessName(request.getBussinessName())) {
            throw new RuntimeException("Username is already taken");
        }
        // Check if the email is already registered
        if(vendorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }
        // Create a new vendor entity and set its properties
        VendorProfile vendor = VendorProfile.builder()
                .bussinessName(request.getBussinessName())
                .email(request.getEmail())
                .bussinessLicenseNumber(request.getBussinessLicenseNumber())
                .password(passwordEncoder.encode(request.getPassword())) // Encode the password
                .phoneNumber(request.getPhoneNumber())
                .build();
        // Save the new vendor to the repository
        vendorRepository.save(vendor);
        // create a new user in the LoginCredRepository
        LoginUserCredRoleCheck loginUser = LoginUserCredRoleCheck.builder()
                .username(request.getBussinessName())
                .role(Role.ROLE_VENDOR) // Set the role to VENDOR
                .entity_id(vendor.getId()) // Set the entity ID to the vendor's ID
                .build();

        // Save the login credentials with the role
        loginCredRepository.save(loginUser);
    }

    @Override
    public JWTResponse login(LoginRequest request) {

        // check if role exists in central login table
        LoginUserCredRoleCheck loginRecord = loginCredRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        Role role = loginRecord.getRole();
        Long entityId = loginRecord.getEntity_id();
        String token ;
        // switch based on the role to fetch the user or vendor
        switch (role) {
            case ROLE_USER:
                User user = userRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    throw new RuntimeException("Invalid username or password");
                }
                 token = jwtUtils.generateJwtToken(user.getUsername(), role);
                return new JWTResponse(token , "Bearer" , user.getId() , user.getUsername() , user.getEmail(), role);
            case ROLE_VENDOR:
                VendorProfile vendor = vendorRepository.findById(entityId)
                        .orElseThrow(() -> new RuntimeException("Vendor not found"));
                if(!passwordEncoder.matches(request.getPassword(), vendor.getPassword())) {
                    throw new RuntimeException("Invalid username or password");
                }
                 token = jwtUtils.generateJwtToken(vendor.getBussinessName(), role);
                return new JWTResponse(token , "Bearer" , vendor.getId() , vendor.getBussinessName() , vendor.getEmail(), role);
            default:
                throw new RuntimeException("Invalid role");
        }
    }
}
