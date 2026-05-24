package com.tmp.authservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String role,
        LocalDateTime createdAt,
        Boolean isActive
) {}