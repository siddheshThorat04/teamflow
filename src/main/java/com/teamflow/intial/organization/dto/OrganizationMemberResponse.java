package com.teamflow.intial.organization.dto;

import com.teamflow.intial.organization.OrgRole;
import com.teamflow.intial.organization.OrganizationMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationMemberResponse {

    private Long userId;
    private String email;
    private String fullName;
    private OrgRole role;

    public static OrganizationMemberResponse fromEntity(OrganizationMember member) {
        OrganizationMemberResponse response = new OrganizationMemberResponse();
        response.setUserId(member.getUser().getId());
        response.setEmail(member.getUser().getEmail());
        response.setFullName(member.getUser().getFullName());
        response.setRole(member.getRole());
        return response;
    }
}