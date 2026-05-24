package com.tmp.taskservice.dto.request;

import com.tmp.taskservice.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private TaskPriority priority;

    private UUID assigneeUserId;
}