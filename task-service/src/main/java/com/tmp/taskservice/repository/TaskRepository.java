package com.tmp.taskservice.repository;

import com.tmp.taskservice.entity.Task;
import com.tmp.taskservice.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);

    List<Task> findByAssigneeUserId(UUID assigneeUserId);

    Optional<Task> findByIdAndProjectId(UUID id, UUID projectId);
}