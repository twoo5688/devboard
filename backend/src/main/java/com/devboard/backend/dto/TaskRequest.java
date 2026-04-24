package com.devboard.backend.dto;

import com.devboard.backend.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequest {

	@NotBlank
	private String title;

	private String description;

	private TaskStatus status;

}
