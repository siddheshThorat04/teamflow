package com.teamflow.intial.organization;

import com.teamflow.intial.organization.dto.AddMemberRequest;
import com.teamflow.intial.organization.dto.CreateOrganizationRequest;
import com.teamflow.intial.organization.dto.OrganizationResponse;
import com.teamflow.intial.user.User;
import com.teamflow.intial.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;
    private final OrganizationAuthorizationService orgAuth;

    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String baseSlug = generateSlug(request.getName());
        String uniqueSlug = ensureUniqueSlug(baseSlug);

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setSlug(uniqueSlug);
        Organization savedOrg = organizationRepository.save(organization);

        OrganizationMember membership = new OrganizationMember();
        membership.setUser(creator);
        membership.setOrganization(savedOrg);
        membership.setRole(OrgRole.OWNER);
        organizationMemberRepository.save(membership);

        return OrganizationResponse.fromEntity(savedOrg);
    }

    public List<OrganizationResponse> getMyOrganizations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return organizationMemberRepository.findByUserIdWithOrganization(user.getId())
                .stream()
                .map(member -> OrganizationResponse.fromEntity(member.getOrganization()))
                .toList();
    }

    // Deliberately public to any authenticated user, not membership-gated —
    // mirrors GitHub's public org profile pattern. Revisit if org visibility
    // needs to become private/invite-only in the future.
    public OrganizationResponse getBySlug(String slug) {
        Organization organization = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        return OrganizationResponse.fromEntity(organization);
    }

    @Transactional
    public void addMember(String orgSlug, AddMemberRequest request, String requesterEmail) {
        Organization organization = orgAuth.requireOrganization(orgSlug);
        User requester = orgAuth.requireUser(requesterEmail);
        orgAuth.requireMinimumRole(requester.getId(), organization.getId(), OrgRole.ADMIN);

        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with that email does not exist"));

        if (organizationMemberRepository.existsByUserIdAndOrganizationId(newMember.getId(), organization.getId())) {
            throw new IllegalArgumentException("User is already a member of this organization");
        }

        OrganizationMember membership = new OrganizationMember();
        membership.setUser(newMember);
        membership.setOrganization(organization);
        membership.setRole(OrgRole.MEMBER);
        organizationMemberRepository.save(membership);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }

    private String ensureUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int suffix = 1;
        while (organizationRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }
        return slug;
    }
}