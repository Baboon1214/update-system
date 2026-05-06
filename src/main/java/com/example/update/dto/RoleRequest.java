package com.example.update.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class RoleRequest {
    @NotBlank(message = "Role name is required")
    private String name;

    private Set<String> permissionNames;

    // getters and setters
}