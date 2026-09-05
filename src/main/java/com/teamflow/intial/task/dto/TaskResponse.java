package com.teamflow.intial.task.dto;

import com.teamflow.intial.task.Task;
import com.teamflow.intial.task.TaskPriority;
import com.teamflow.intial.task.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class TaskResponse {

    private Long id;
    private String taskKey;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assigneeId;
    private String assigneeName;
    private Long reporterId;
    private String reporterName;
    private LocalDate dueDate;
    private Instant createdAt;

    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTaskKey(task.getProject().getKey() + "-" + task.getTaskNumber());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());

        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
            response.setAssigneeName(task.getAssignee().getFullName());
        }

        response.setReporterId(task.getReporter().getId());
        response.setReporterName(task.getReporter().getFullName());

        return response;
    }
}
