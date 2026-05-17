package com.example.update.enums;

public enum UpdateType {
    MANDATORY,    // Принудительное обновление (force)
    OPTIONAL,     // Рекомендованное (можно отложить)
    DEPRECATED;   // Устаревшее (скоро перестанет работать)

public static UpdateType fromString(String text) {
    if (text == null) {
        return OPTIONAL;
    }

    try {
        return UpdateType.valueOf(text.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Unknown update type: " + text);
    }
    }
}
