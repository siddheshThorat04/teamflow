package com.teamflow.intial.project;

import com.teamflow.intial.organization.Organization;
import com.teamflow.intial.organization.OrganizationMemberRepository;
import com.teamflow.intial.organization.OrganizationRepository;
import com.teamflow.intial.project.dto.CreateProjectRequest;
import com.teamflow.intial.project.dto.ProjectResponse;
import com.teamflow.intial.user.User;
import com.teamflow.intial.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(String orgSlug, CreateProjectRequest request, String requesterEmail) {
        Organization organization = organizationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isMember = organizationMemberRepository
                .existsByUserIdAndOrganizationId(requester.getId(), organization.getId());
        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this organization");
        }

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
        Organization organization = organizationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isMember = organizationMemberRepository
                .existsByUserIdAndOrganizationId(requester.getId(), organization.getId());
        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this organization");
        }

        return projectRepository.findByOrganizationId(organization.getId())
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }
}