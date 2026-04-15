package com.devboard.backend.service;

import com.devboard.backend.dto.TaskRequest;
import com.devboard.backend.entity.Task;
import com.devboard.backend.entity.TaskStatus;
import com.devboard.backend.repository.ProjectRepository;
import com.devboard.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public List<Task> getProjectTasks(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task createTask(Long projectId, TaskRequest request) {
        var project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        Task task = Task.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
            .project(project)
            .build();
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.deleteById(taskId);
    }
}