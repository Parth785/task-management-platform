package com.tmp.taskservice.dto.response;

import com.tmp.taskservice.enums.TaskPriority;
import com.tmp.taskservice.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        UUID assigneeUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}