package com.devboard.backend.service;

import com.devboard.backend.dto.ProjectRequest;
import com.devboard.backend.entity.Project;
import com.devboard.backend.entity.User;
import com.devboard.backend.repository.ProjectRepository;
import com.devboard.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectServiceTest {

	private static final String OWNER_EMAIL = "project-owner@example.com";

	private static final String OTHER_EMAIL = "project-other@example.com";

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserRepository userRepository;

	private User owner;

	private Project project;

	@BeforeEach
	void setUp() {
		owner = userRepository.save(user("Owner", OWNER_EMAIL));
		userRepository.save(user("Other", OTHER_EMAIL));
		project = projectRepository.save(Project.builder()
			.name("Original project")
			.description("Original description")
			.owner(owner)
			.build());
	}

	@Test
	void listsOnlyProjectsOwnedByUser() {
		User other = userRepository.findByEmail(OTHER_EMAIL).orElseThrow();
		projectRepository.save(Project.builder().name("Foreign project").owner(other).build());

		List<Project> projects = projectService.getUserProjects(OWNER_EMAIL);

		assertThat(projects).extracting(Project::getId).containsExactly(project.getId());
	}

	@Test
	void getsOwnedProject() {
		Project result = projectService.getProject(project.getId(), OWNER_EMAIL);

		assertThat(result.getId()).isEqualTo(project.getId());
		assertThat(result.getName()).isEqualTo("Original project");
	}

	@Test
	void rejectsReadingProjectOwnedByAnotherUser() {
		assertThatThrownBy(() -> projectService.getProject(project.getId(), OTHER_EMAIL))
			.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void createsProjectForAuthenticatedUser() {
		Project created = projectService.createProject(request("Created project", "Created description"), OWNER_EMAIL);

		assertThat(created.getOwner().getId()).isEqualTo(owner.getId());
		assertThat(created.getName()).isEqualTo("Created project");
	}

	@Test
	void updatesOwnedProject() {
		Project updated = projectService.updateProject(project.getId(),
			request("Updated project", "Updated description"), OWNER_EMAIL);

		assertThat(updated.getName()).isEqualTo("Updated project");
		assertThat(updated.getDescription()).isEqualTo("Updated description");
	}

	@Test
	void rejectsUpdatingProjectOwnedByAnotherUser() {
		assertThatThrownBy(() -> projectService.updateProject(project.getId(),
				request("Unauthorized update", null), OTHER_EMAIL))
			.isInstanceOf(AccessDeniedException.class);
		assertThat(projectRepository.findById(project.getId()).orElseThrow().getName()).isEqualTo("Original project");
	}

	@Test
	void deletesOwnedProject() {
		projectService.deleteProject(project.getId(), OWNER_EMAIL);

		assertThat(projectRepository.findById(project.getId())).isEmpty();
	}

	@Test
	void rejectsDeletingProjectOwnedByAnotherUser() {
		assertThatThrownBy(() -> projectService.deleteProject(project.getId(), OTHER_EMAIL))
			.isInstanceOf(AccessDeniedException.class);
		assertThat(projectRepository.findById(project.getId())).isPresent();
	}

	private User user(String name, String email) {
		return User.builder().name(name).email(email).password("encoded-password").build();
	}

	private ProjectRequest request(String name, String description) {
		ProjectRequest request = new ProjectRequest();
		request.setName(name);
		request.setDescription(description);
		return request;
	}

}
