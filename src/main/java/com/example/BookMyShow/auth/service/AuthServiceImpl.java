package com.example.BookMyShow.auth.service;

import com.example.BookMyShow.auth.dto.JWTResponse;
import com.example.BookMyShow.auth.dto.LoginRequest;
import com.example.BookMyShow.auth.dto.SignUpRequest;
import com.example.BookMyShow.auth.entity.User;
import com.example.BookMyShow.auth.repository.UserRepository;
import com.example.BookMyShow.auth.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void register(SignUpRequest request) {
        // Check if the user already exists by username or email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.findByEmail(request.getEmail())) {
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
    }

    @Override
    public JWTResponse login(LoginRequest request) {
        // Find the user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Check if the provided password matches the stored password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        // Get all the roles of the user
        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        // Generate a JWT token for the user
        String token = jwtUtils.generateJwtToken(user.getUsername(), roles);

        return new JWTResponse(token , "Bearer" , user.getId() , user.getUsername() , user.getEmail(), roles);
    }
}
