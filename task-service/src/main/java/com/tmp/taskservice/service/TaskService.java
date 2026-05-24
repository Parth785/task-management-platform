package com.tmp.taskservice.service;

import com.tmp.taskservice.dto.request.AssignTaskRequest;
import com.tmp.taskservice.dto.request.CreateTaskRequest;
import com.tmp.taskservice.dto.request.UpdateTaskRequest;
import com.tmp.taskservice.dto.request.UpdateTaskStatusRequest;
import com.tmp.taskservice.dto.response.TaskResponse;
import com.tmp.taskservice.entity.Project;
import com.tmp.taskservice.entity.Task;
import com.tmp.taskservice.enums.TaskStatus;
import com.tmp.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    public TaskResponse createTask(UUID projectId, CreateTaskRequest request) {
        Project project = projectService.findProjectById(projectId);

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .assigneeUserId(request.getAssigneeUserId())
                .build();

        return mapToResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getTasksByProject(UUID projectId, TaskStatus status) {
        projectService.findProjectById(projectId); // validate project exists

        List<Task> tasks = (status != null)
                ? taskRepository.findByProjectIdAndStatus(projectId, status)
                : taskRepository.findByProjectId(projectId);

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(UUID projectId, UUID taskId) {
        Task task = findTaskByIdAndProjectId(taskId, projectId);
        return mapToResponse(task);
    }

    public TaskResponse updateTask(UUID projectId, UUID taskId, UpdateTaskRequest request) {
        Task task = findTaskByIdAndProjectId(taskId, projectId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        return mapToResponse(taskRepository.save(task));
    }

    public TaskResponse updateTaskStatus(UUID projectId, UUID taskId,
                                         UpdateTaskStatusRequest request) {
        Task task = findTaskByIdAndProjectId(taskId, projectId);

        if (!task.getStatus().canTransitionTo(request.getStatus())) {
            throw new IllegalArgumentException(
                    "Invalid transition: " + task.getStatus() + " → " + request.getStatus()
            );
        }

        task.setStatus(request.getStatus());
        return mapToResponse(taskRepository.save(task));
    }

    public TaskResponse assignTask(UUID projectId, UUID taskId, AssignTaskRequest request) {
        Task task = findTaskByIdAndProjectId(taskId, projectId);

        // validate assignee exists and is active in auth-service
        validateUserExists(request.getAssigneeUserId());

        task.setAssigneeUserId(request.getAssigneeUserId());
        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(UUID projectId, UUID taskId) {
        Task task = findTaskByIdAndProjectId(taskId, projectId);
        taskRepository.delete(task);
    }

    public List<TaskResponse> getMyTasks(UUID userId) {
        return taskRepository.findByAssigneeUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateUserExists(UUID userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(
                    authServiceUrl + "/api/v1/users/" + userId,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Assignee user not found with id: " + userId);
        } catch (Exception e) {
            throw new RuntimeException("Could not validate assignee user: " + e.getMessage());
        }
    }

    private Task findTaskByIdAndProjectId(UUID taskId, UUID projectId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new RuntimeException(
                        "Task not found with id: " + taskId + " in project: " + projectId));
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getAssigneeUserId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}