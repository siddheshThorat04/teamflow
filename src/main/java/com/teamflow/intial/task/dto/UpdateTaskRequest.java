package com.teamflow.intial.task.dto;

import com.teamflow.intial.task.TaskPriority;
import com.teamflow.intial.task.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTaskRequest {

    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assigneeId;
    private LocalDate dueDate;
}