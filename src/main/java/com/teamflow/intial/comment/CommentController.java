package com.teamflow.intial.comment;

import com.teamflow.intial.comment.dto.CommentResponse;
import com.teamflow.intial.comment.dto.CreateCommentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        CommentResponse response = commentService.addComment(taskId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> list(
            @PathVariable Long taskId,
            Authentication authentication
    ) {
        List<CommentResponse> comments = commentService.getCommentsForTask(taskId, authentication.getName());
        return ResponseEntity.ok(comments);
    }
}