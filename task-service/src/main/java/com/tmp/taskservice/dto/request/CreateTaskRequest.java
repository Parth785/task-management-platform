package com.tmp.taskservice.dto.request;

import com.tmp.taskservice.enums.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private TaskPriority priority;

    private UUID assigneeUserId;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}