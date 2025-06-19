package com.example.BookMyShow.auth.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
// JwtUtils is a utility class for handling JSON Web Tokens (JWT).
public class JwtUtils {

    private final Key jwtSecretKey;
    private final int jwtExpirationMs;

    public JwtUtils(Key jwtSecretKey, int jwtExpirationMs) {
        this.jwtSecretKey = jwtSecretKey;
        this.jwtExpirationMs = jwtExpirationMs;
    }
    /**
     * Generate a JWT token containing username and roles as claims.
     */
    public String generateJwtToken(String username , List<String> roles){
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Extract the username from the JWT token.
     */
    public String getUsernameFromJwtToken(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    /**
     * Extract the roles from the JWT token.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        return parseClaims(token).getBody().get("roles", List.class);
    }
    /**
     * Validate the JWT token.
     */
    public boolean validateJwtToken(String authToken){
        try {
            parseClaims(authToken);
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            // Log the exception or handle it as needed
            System.err.println("JWT token is invalid: " + e.getMessage()) ;
        }
        return false;
    }
    /**
     * Parse the JWT token and return the claims.
     */
    private Jws<Claims> parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }


}
