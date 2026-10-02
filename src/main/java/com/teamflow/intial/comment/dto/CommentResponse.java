package com.teamflow.intial.comment.dto;

import com.teamflow.intial.comment.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CommentResponse {

    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private Instant createdAt;

    public static CommentResponse fromEntity(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorId(comment.getAuthor().getId());
        response.setAuthorName(comment.getAuthor().getFullName());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}