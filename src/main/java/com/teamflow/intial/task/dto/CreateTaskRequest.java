package com.teamflow.intial.task.dto;

import com.teamflow.intial.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskPriority priority;

    private Long assigneeId;

    private LocalDate dueDate;
}
