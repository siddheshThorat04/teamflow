package com.teamflow.intial.organization.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OrganizationResponse {

    private Long id;
    private String name;
    private String slug;
    private Instant createdAt;

    public static OrganizationResponse fromEntity(com.teamflow.intial.organization.Organization org) {
        OrganizationResponse response = new OrganizationResponse();
        response.setId(org.getId());
        response.setName(org.getName());
        response.setSlug(org.getSlug());
        response.setCreatedAt(org.getCreatedAt());
        return response;
    }
}