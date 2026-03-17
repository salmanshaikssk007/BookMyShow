package com.example.BookMyShow.auth.util;

import com.example.BookMyShow.auth.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
// JwtUtils is a utility class for handling JSON Web Tokens (JWT).
public class JwtUtils {

    private final Key jwtSecretKey;
    private final long jwtExpirationMs;

    public JwtUtils(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }
    /**
     * Generate a JWT token containing username and roles as claims.
     */
    public String generateJwtToken(String username , Role role){
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
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
    public String getRolesFromJwtToken(String token) {
        return parseClaims(token).getBody().get("role", String.class);
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
