package com.tmp.taskservice.dto.request;

import com.tmp.taskservice.enums.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private TaskPriority priority;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}