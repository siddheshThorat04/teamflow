package com.teamflow.intial.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProjectResponse {

    private Long id;
    private String name;
    private String key;
    private String description;
    private Instant createdAt;

    public static ProjectResponse fromEntity(com.teamflow.intial.project.Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setKey(project.getKey());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }
}