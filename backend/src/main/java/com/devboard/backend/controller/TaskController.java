package com.devboard.backend.controller;

import com.devboard.backend.dto.TaskRequest;
import com.devboard.backend.entity.Task;
import com.devboard.backend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
	public ResponseEntity<List<Task>> getTasks(@PathVariable Long projectId,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(taskService.getProjectTasks(projectId, userDetails.getUsername()));
	}

	@PostMapping
	@Operation(summary = "Create task")
	public ResponseEntity<Task> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(taskService.createTask(projectId, request, userDetails.getUsername()));
	}

	@PutMapping("/{taskId}")
	@Operation(summary = "Update task")
	public ResponseEntity<Task> updateTask(@PathVariable Long projectId, @PathVariable Long taskId,
			@Valid @RequestBody TaskRequest request, @AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(taskService.updateTask(projectId, taskId, request, userDetails.getUsername()));
	}

	@DeleteMapping("/{taskId}")
	@Operation(summary = "Delete task")
	public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId,
			@AuthenticationPrincipal UserDetails userDetails) {
		taskService.deleteTask(projectId, taskId, userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}

}
