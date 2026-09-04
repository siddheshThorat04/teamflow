package com.teamflow.intial.organization;

import com.teamflow.intial.organization.dto.CreateOrganizationRequest;
import com.teamflow.intial.organization.dto.OrganizationResponse;
import com.teamflow.intial.user.User;
import com.teamflow.intial.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

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