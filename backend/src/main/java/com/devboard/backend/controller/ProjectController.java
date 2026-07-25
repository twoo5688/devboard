package com.devboard.backend.controller;

import com.devboard.backend.dto.ProjectRequest;
import com.devboard.backend.entity.Project;
import com.devboard.backend.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project CRUD for the authenticated user")
public class ProjectController {

	private final ProjectService projectService;

	@GetMapping
	@Operation(summary = "List projects", description = "Returns all projects owned by the current user")
	public ResponseEntity<List<Project>> getProjects(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(projectService.getUserProjects(userDetails.getUsername()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get project")
	public ResponseEntity<Project> getProject(@PathVariable Long id,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(projectService.getProject(id, userDetails.getUsername()));
	}

	@PostMapping
	@Operation(summary = "Create project")
	public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(projectService.createProject(request, userDetails.getUsername()));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update project")
	public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(projectService.updateProject(id, request, userDetails.getUsername()));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete project")
	public ResponseEntity<Void> deleteProject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
		projectService.deleteProject(id, userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}

}
