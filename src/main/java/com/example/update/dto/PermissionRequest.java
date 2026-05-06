package com.example.update.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionRequest {
    @NotBlank(message = "Permission name is required")
    private String name;

    private String description;

    // getters and setters
}