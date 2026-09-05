package com.teamflow.intial.project;

import com.teamflow.intial.organization.Organization;
import com.teamflow.intial.organization.OrganizationAuthorizationService;
import com.teamflow.intial.project.dto.CreateProjectRequest;
import com.teamflow.intial.project.dto.ProjectResponse;
import com.teamflow.intial.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationAuthorizationService orgAuth;

    @Transactional
    public ProjectResponse createProject(String orgSlug, CreateProjectRequest request, String requesterEmail) {
        Organization organization = orgAuth.requireOrganization(orgSlug);
        User requester = orgAuth.requireUser(requesterEmail);
        orgAuth.requireMembership(requester.getId(), organization.getId());

        String normalizedKey = request.getKey().toUpperCase();
        if (projectRepository.existsByOrganizationIdAndKey(organization.getId(), normalizedKey)) {
            throw new IllegalArgumentException("A project with that key already exists in this organization");
        }

        Project project = new Project();
        project.setOrganization(organization);
        project.setName(request.getName());
        project.setKey(normalizedKey);
        project.setDescription(request.getDescription());

        Project saved = projectRepository.save(project);
        return ProjectResponse.fromEntity(saved);
    }

    public List<ProjectResponse> getProjectsForOrganization(String orgSlug, String requesterEmail) {
        Organization organization = orgAuth.requireOrganization(orgSlug);
        User requester = orgAuth.requireUser(requesterEmail);
        orgAuth.requireMembership(requester.getId(), organization.getId());

        return projectRepository.findByOrganizationId(organization.getId())
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }
}