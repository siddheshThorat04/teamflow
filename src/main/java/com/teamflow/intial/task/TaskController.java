package com.teamflow.intial.task;

import com.teamflow.intial.task.dto.CreateTaskRequest;
import com.teamflow.intial.task.dto.TaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {
        TaskResponse response = taskService.createTask(projectId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        List<TaskResponse> tasks = taskService.getTasksForProject(projectId, authentication.getName());
        return ResponseEntity.ok(tasks);
    }
}