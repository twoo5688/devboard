package com.devboard.backend.service;

import com.devboard.backend.dto.TaskRequest;
import com.devboard.backend.entity.Project;
import com.devboard.backend.entity.Task;
import com.devboard.backend.entity.TaskStatus;
import com.devboard.backend.entity.User;
import com.devboard.backend.repository.ProjectRepository;
import com.devboard.backend.repository.TaskRepository;
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
class TaskServiceTest {

	private static final String OWNER_EMAIL = "owner@example.com";

	private static final String OTHER_EMAIL = "other@example.com";

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserRepository userRepository;

	private Project project;

	@BeforeEach
	void setUp() {
		User owner = userRepository.save(User.builder()
			.name("Owner")
			.email(OWNER_EMAIL)
			.password("encoded-password")
			.build());
		userRepository.save(User.builder()
			.name("Other")
			.email(OTHER_EMAIL)
			.password("encoded-password")
			.build());
		project = projectRepository.save(Project.builder().name("Project").owner(owner).build());
	}

	@Test
	void listsTasksOnlyForOwnedProject() {
		Task task = taskRepository.save(task("Task", project));

		List<Task> tasks = taskService.getProjectTasks(project.getId(), OWNER_EMAIL);

		assertThat(tasks).extracting(Task::getId).containsExactly(task.getId());
	}

	@Test
	void rejectsTaskAccessWhenProjectIsNotOwned() {
		taskRepository.save(task("Task", project));

		assertThatThrownBy(() -> taskService.getProjectTasks(project.getId(), OTHER_EMAIL))
			.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void createsTaskUnderOwnedProject() {
		Task created = taskService.createTask(project.getId(), request("New task", TaskStatus.IN_PROGRESS), OWNER_EMAIL);

		assertThat(created.getProject().getId()).isEqualTo(project.getId());
		assertThat(created.getTitle()).isEqualTo("New task");
		assertThat(created.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
	}

	@Test
	void rejectsTaskCreationWhenProjectIsNotOwned() {
		long taskCount = taskRepository.count();

		assertThatThrownBy(
				() -> taskService.createTask(project.getId(), request("New task", TaskStatus.TODO), OTHER_EMAIL))
			.isInstanceOf(AccessDeniedException.class);
		assertThat(taskRepository.count()).isEqualTo(taskCount);
	}

	@Test
	void updatesOnlyTaskBelongingToAuthorizedUrlProject() {
		Task existing = taskRepository.save(task("Old", project));

		Task updated = taskService.updateTask(project.getId(), existing.getId(),
				request("Updated", TaskStatus.DONE), OWNER_EMAIL);

		assertThat(updated.getTitle()).isEqualTo("Updated");
		assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);
	}

	@Test
	void rejectsTaskFromDifferentProject() {
		User owner = userRepository.findByEmail(OWNER_EMAIL).orElseThrow();
		Project otherProject = projectRepository.save(Project.builder().name("Other project").owner(owner).build());
		Task otherTask = taskRepository.save(task("Other task", otherProject));

		assertThatThrownBy(() -> taskService.updateTask(project.getId(), otherTask.getId(),
				request("Updated", TaskStatus.DONE), OWNER_EMAIL))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Task not found");
		assertThat(taskRepository.findById(otherTask.getId()).orElseThrow().getTitle()).isEqualTo("Other task");
	}

	@Test
	void deletesScopedTaskEntity() {
		Task existing = taskRepository.save(task("Task", project));

		taskService.deleteTask(project.getId(), existing.getId(), OWNER_EMAIL);

		assertThat(taskRepository.findById(existing.getId())).isEmpty();
	}

	private Task task(String title, Project taskProject) {
		return Task.builder().title(title).status(TaskStatus.TODO).project(taskProject).build();
	}

	private TaskRequest request(String title, TaskStatus status) {
		TaskRequest request = new TaskRequest();
		request.setTitle(title);
		request.setDescription("Description");
		request.setStatus(status);
		return request;
	}

}
