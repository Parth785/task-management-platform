package com.tmp.taskservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID ownerUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}