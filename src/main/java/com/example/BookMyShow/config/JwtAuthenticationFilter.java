package com.example.BookMyShow.config;

import com.example.BookMyShow.auth.service.TokenBlacklistService;
import com.example.BookMyShow.auth.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7).trim();
            if (jwtUtils.validateJwtToken(jwtToken)) {
                if (!"access".equals(jwtUtils.getTokenType(jwtToken))) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token type");
                    return;
                }
                String jti = jwtUtils.getJtiFromToken(jwtToken);
                if (tokenBlacklistService.isBlacklisted(jti)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
                    return;
                }

                String username = jwtUtils.getUsernameFromJwtToken(jwtToken);
                String roles = jwtUtils.getRolesFromJwtToken(jwtToken);

                var authority = new SimpleGrantedAuthority(roles);
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        java.util.Collections.singletonList(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
