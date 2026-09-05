package com.teamflow.intial.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    @NotBlank(message = "Project key is required")
    @Size(min = 2, max = 10, message = "Project key must be 2-10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Project key must be uppercase letters/numbers only")
    private String key;

    private String description;
}