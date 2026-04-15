package com.devboard.backend.service;

import com.devboard.backend.dto.ProjectRequest;
import com.devboard.backend.entity.Project;
import com.devboard.backend.entity.User;
import com.devboard.backend.repository.ProjectRepository;
import com.devboard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Project> getUserProjects(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return projectRepository.findByOwnerId(user.getId());
    }

    public Project createProject(ProjectRequest request, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = Project.builder()
            .name(request.getName())
            .description(request.getDescription())
            .owner(user)
            .build();
        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId, String email) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getEmail().equals(email)) {
            throw new RuntimeException("Not authorized");
        }
        projectRepository.delete(project);
    }
}