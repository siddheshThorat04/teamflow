package com.teamflow.intial.comment;

import com.teamflow.intial.comment.dto.CommentResponse;
import com.teamflow.intial.comment.dto.CreateCommentRequest;
import com.teamflow.intial.organization.OrganizationAuthorizationService;
import com.teamflow.intial.task.Task;
import com.teamflow.intial.task.TaskRepository;
import com.teamflow.intial.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final OrganizationAuthorizationService orgAuth;

    @Transactional
    public CommentResponse addComment(Long taskId, CreateCommentRequest request, String authorEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User author = orgAuth.requireUser(authorEmail);
        orgAuth.requireMembership(author.getId(), task.getProject().getOrganization().getId());

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setContent(request.getContent());

        Comment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved);
    }

    public List<CommentResponse> getCommentsForTask(Long taskId, String requesterEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User requester = orgAuth.requireUser(requesterEmail);
        orgAuth.requireMembership(requester.getId(), task.getProject().getOrganization().getId());

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(task.getId())
                .stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }
}