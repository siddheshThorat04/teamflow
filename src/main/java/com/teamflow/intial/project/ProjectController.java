package com.teamflow.intial.project;

import com.teamflow.intial.project.dto.CreateProjectRequest;
import com.teamflow.intial.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{orgSlug}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @PathVariable String orgSlug,
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication
    ) {
        ProjectResponse response = projectService.createProject(orgSlug, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> list(
            @PathVariable String orgSlug,
            Authentication authentication
    ) {
        List<ProjectResponse> projects = projectService.getProjectsForOrganization(orgSlug, authentication.getName());
        return ResponseEntity.ok(projects);
    }
}