package com.tmp.taskservice.service;

import com.tmp.taskservice.dto.request.CreateProjectRequest;
import com.tmp.taskservice.dto.request.UpdateProjectRequest;
import com.tmp.taskservice.dto.response.ProjectResponse;
import com.tmp.taskservice.entity.Project;
import com.tmp.taskservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectResponse createProject(CreateProjectRequest request, UUID ownerUserId) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerUserId(ownerUserId)
                .build();

        return mapToResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(UUID id) {
        Project project = findProjectById(id);
        return mapToResponse(project);
    }

    public ProjectResponse updateProject(UUID id, UpdateProjectRequest request) {
        Project project = findProjectById(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return mapToResponse(projectRepository.save(project));
    }

    public void deleteProject(UUID id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }

    // reusable internal method — used by TaskService too
    public Project findProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwnerUserId(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}