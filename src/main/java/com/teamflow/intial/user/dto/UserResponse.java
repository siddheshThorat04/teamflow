package com.teamflow.intial.user.dto;

import com.teamflow.intial.user.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private Instant createdAt;

    public static UserResponse fromEntity(com.teamflow.intial.user.User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}