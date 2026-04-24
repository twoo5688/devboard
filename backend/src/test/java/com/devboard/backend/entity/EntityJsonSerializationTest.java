package com.devboard.backend.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class EntityJsonSerializationTest {

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Test
	void projectSerializesWithoutTasksField() throws Exception {
		User owner = User.builder()
			.id(1L)
			.name("Owner")
			.email("owner@example.com")
			.password("secret")
			.build();
		Project project = Project.builder()
			.id(10L)
			.name("Demo")
			.description("Desc")
			.owner(owner)
			.build();
		Task task = Task.builder()
			.id(20L)
			.title("Task")
			.description("T")
			.status(TaskStatus.TODO)
			.project(project)
			.build();
		project.setTasks(List.of(task));

		String json = objectMapper.writeValueAsString(project);

		assertThat(json).contains("\"name\":\"Demo\"");
		assertThat(json).doesNotContain("\"tasks\"");
	}

	@Test
	void taskSerializesWithoutProjectField() throws Exception {
		Project project = Project.builder().id(1L).name("P").description("d").build();
		Task task = Task.builder()
			.id(2L)
			.title("T")
			.description("d")
			.status(TaskStatus.IN_PROGRESS)
			.project(project)
			.build();

		String json = objectMapper.writeValueAsString(task);

		assertThat(json).contains("\"title\":\"T\"");
		assertThat(json).doesNotContain("\"project\"");
	}

}
