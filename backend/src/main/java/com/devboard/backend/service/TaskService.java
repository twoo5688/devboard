package com.devboard.backend.service;

import com.devboard.backend.dto.TaskRequest;
import com.devboard.backend.entity.Project;
import com.devboard.backend.entity.Task;
import com.devboard.backend.entity.TaskStatus;
import com.devboard.backend.repository.ProjectRepository;
import com.devboard.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;

	private final ProjectRepository projectRepository;

	public List<Task> getProjectTasks(Long projectId, String email) {
		getOwnedProject(projectId, email);
		return taskRepository.findByProjectId(projectId);
	}

	public Task createTask(Long projectId, TaskRequest request, String email) {
		Project project = getOwnedProject(projectId, email);
		Task task = Task.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
			.project(project)
			.build();
		return taskRepository.save(task);
	}

	public Task updateTask(Long projectId, Long taskId, TaskRequest request, String email) {
		getOwnedProject(projectId, email);
		Task task = getProjectTask(projectId, taskId);
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		if (request.getStatus() != null)
			task.setStatus(request.getStatus());
		return taskRepository.save(task);
	}

	public void deleteTask(Long projectId, Long taskId, String email) {
		getOwnedProject(projectId, email);
		taskRepository.delete(getProjectTask(projectId, taskId));
	}

	private Project getOwnedProject(Long projectId, String email) {
		return projectRepository.findByIdAndOwnerEmail(projectId, email)
			.orElseThrow(() -> new AccessDeniedException("Project not found or access denied"));
	}

	private Task getProjectTask(Long projectId, Long taskId) {
		return taskRepository.findByIdAndProjectId(taskId, projectId)
			.orElseThrow(() -> new RuntimeException("Task not found"));
	}

}
