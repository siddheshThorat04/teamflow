package com.teamflow.intial.task;

import com.teamflow.intial.organization.Organization;
import com.teamflow.intial.organization.OrganizationAuthorizationService;
import com.teamflow.intial.project.Project;
import com.teamflow.intial.project.ProjectRepository;
import com.teamflow.intial.task.dto.CreateTaskRequest;
import com.teamflow.intial.task.dto.TaskResponse;
import com.teamflow.intial.user.User;
import com.teamflow.intial.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.teamflow.intial.task.dto.UpdateTaskRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final OrganizationAuthorizationService orgAuth;

    @Transactional
    public TaskResponse createTask(Long projectId, CreateTaskRequest request, String reporterEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        User reporter = orgAuth.requireUser(reporterEmail);
        orgAuth.requireMembership(reporter.getId(), project.getOrganization().getId());

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            orgAuth.requireMembership(assignee.getId(), project.getOrganization().getId());
        }

        Integer nextTaskNumber = taskRepository.findMaxTaskNumberForProject(project.getId()) + 1;

        Task task = new Task();
        task.setProject(project);
        task.setTaskNumber(nextTaskNumber);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setReporter(reporter);
        task.setAssignee(assignee);

        Task saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }
    @Transactional
public TaskResponse updateTask(Long projectId, Long taskId, UpdateTaskRequest request, String requesterEmail) {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

    if (!task.getProject().getId().equals(projectId)) {
        throw new IllegalArgumentException("Task does not belong to this project");
    }

    User requester = orgAuth.requireUser(requesterEmail);
    orgAuth.requireMembership(requester.getId(), task.getProject().getOrganization().getId());

    if (request.getTitle() != null) {
        task.setTitle(request.getTitle());
    }
    if (request.getDescription() != null) {
        task.setDescription(request.getDescription());
    }
    if (request.getStatus() != null) {
        task.setStatus(request.getStatus());
    }
    if (request.getPriority() != null) {
        task.setPriority(request.getPriority());
    }
    if (request.getDueDate() != null) {
        task.setDueDate(request.getDueDate());
    }
    if (request.getAssigneeId() != null) {
        User newAssignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        orgAuth.requireMembership(newAssignee.getId(), task.getProject().getOrganization().getId());
        task.setAssignee(newAssignee);
    }

    Task saved = taskRepository.save(task);
    return TaskResponse.fromEntity(saved);
}

    public List<TaskResponse> getTasksForProject(Long projectId, String requesterEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        User requester = orgAuth.requireUser(requesterEmail);
        orgAuth.requireMembership(requester.getId(), project.getOrganization().getId());

        return taskRepository.findByProjectId(project.getId())
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
}