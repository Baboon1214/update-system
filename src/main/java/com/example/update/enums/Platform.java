package com.example.update.enums;

public enum Platform {
    ANDROID,
    IOS,
    WINDOWS,
    MACOS,
    LINUX;

    public static Platform fromString(String text) {
        for (Platform p : Platform.values()) {
            if (p.name().equalsIgnoreCase(text)) {
                return p;
            }
        }
        return null;
    }
}