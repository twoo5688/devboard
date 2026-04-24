package com.devboard.backend.controller;

import com.devboard.backend.dto.TaskRequest;
import com.devboard.backend.entity.Task;
import com.devboard.backend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Tasks for a project")
public class TaskController {

	private final TaskService taskService;

	@GetMapping
	@Operation(summary = "List tasks")
	public ResponseEntity<List<Task>> getTasks(@PathVariable Long projectId) {
		return ResponseEntity.ok(taskService.getProjectTasks(projectId));
	}

	@PostMapping
	@Operation(summary = "Create task")
	public ResponseEntity<Task> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request) {
		return ResponseEntity.ok(taskService.createTask(projectId, request));
	}

	@PutMapping("/{taskId}")
	@Operation(summary = "Update task")
	public ResponseEntity<Task> updateTask(@PathVariable Long projectId, @PathVariable Long taskId,
			@Valid @RequestBody TaskRequest request) {
		return ResponseEntity.ok(taskService.updateTask(taskId, request));
	}

	@DeleteMapping("/{taskId}")
	@Operation(summary = "Delete task")
	public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
		taskService.deleteTask(taskId);
		return ResponseEntity.noContent().build();
	}

}
