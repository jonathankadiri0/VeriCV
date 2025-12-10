package com.vericv.platform.util;

import com.vericv.platform.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {

    private final JwtTokenProvider tokenProvider;

    public AuthHelper(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getCredentials() == null) {
            throw new RuntimeException("Authentication required");
        }

        String token = authentication.getCredentials().toString();
        Long userId = tokenProvider.getUserIdFromToken(token);

        if (userId == null) {
            throw new RuntimeException("Invalid token: userId not found");
        }

        return userId;
    }
}