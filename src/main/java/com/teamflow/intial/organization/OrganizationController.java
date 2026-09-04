package com.teamflow.intial.organization;

import com.teamflow.intial.organization.dto.AddMemberRequest;
import com.teamflow.intial.organization.dto.CreateOrganizationRequest;
import com.teamflow.intial.organization.dto.OrganizationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.teamflow.intial.organization.dto.AddMemberRequest;
import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
            @Valid @RequestBody CreateOrganizationRequest request,
            Authentication authentication
    ) {
        OrganizationResponse response = organizationService.createOrganization(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> listMine(Authentication authentication) {
        List<OrganizationResponse> organizations = organizationService.getMyOrganizations(authentication.getName());
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<OrganizationResponse> getBySlug(@PathVariable String slug) {
        OrganizationResponse response = organizationService.getBySlug(slug);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{slug}/members")
    public ResponseEntity<Void> addMember(
        @PathVariable String slug,
        @Valid @RequestBody AddMemberRequest request,
        Authentication authentication
    ) {
        organizationService.addMember(slug, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}