package com.example.update.dto;

import lombok.Data;

@Data
public class UpdateResponse {
    private boolean updateAvailable;
    private String latestVersion;
    private String updateType;
    private String changelog;
    private boolean forceUpdate;
}