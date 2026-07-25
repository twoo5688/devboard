package com.devboard.backend.service;

import com.devboard.backend.dto.ProjectRequest;
import com.devboard.backend.entity.Project;
import com.devboard.backend.entity.User;
import com.devboard.backend.repository.ProjectRepository;
import com.devboard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;

	private final UserRepository userRepository;

	public List<Project> getUserProjects(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		return projectRepository.findByOwnerId(user.getId());
	}

	public Project getProject(Long projectId, String email) {
		return getOwnedProject(projectId, email);
	}

	public Project createProject(ProjectRequest request, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		Project project = Project.builder()
			.name(request.getName())
			.description(request.getDescription())
			.owner(user)
			.build();
		return projectRepository.save(project);
	}

	public Project updateProject(Long projectId, ProjectRequest request, String email) {
		Project project = getOwnedProject(projectId, email);
		project.setName(request.getName());
		project.setDescription(request.getDescription());
		return projectRepository.save(project);
	}

	public void deleteProject(Long projectId, String email) {
		projectRepository.delete(getOwnedProject(projectId, email));
	}

	private Project getOwnedProject(Long projectId, String email) {
		return projectRepository.findByIdAndOwnerEmail(projectId, email)
			.orElseThrow(() -> new AccessDeniedException("Project not found or access denied"));
	}

}
