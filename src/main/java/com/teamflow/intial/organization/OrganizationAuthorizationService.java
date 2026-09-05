package com.teamflow.intial.organization;

import com.teamflow.intial.user.User;
import com.teamflow.intial.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationAuthorizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    public Organization requireOrganization(String orgSlug) {
        return organizationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
    }

    public User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public OrganizationMember requireMembership(Long userId, Long organizationId) {
        return organizationMemberRepository.findByUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this organization"));
    }

    public void requireMinimumRole(Long userId, Long organizationId, OrgRole minimumRole) {
        OrganizationMember membership = requireMembership(userId, organizationId);
        boolean hasEnoughPower = switch (minimumRole) {
            case MEMBER -> true; // any role qualifies
            case ADMIN -> membership.getRole() == OrgRole.ADMIN || membership.getRole() == OrgRole.OWNER;
            case OWNER -> membership.getRole() == OrgRole.OWNER;
        };
        if (!hasEnoughPower) {
            throw new AccessDeniedException("Insufficient permissions for this action");
        }
    }
}