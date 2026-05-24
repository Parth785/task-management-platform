package com.tmp.authservice.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        String role
) {}