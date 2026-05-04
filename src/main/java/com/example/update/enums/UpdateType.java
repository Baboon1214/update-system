package com.example.update.enums;

public enum UpdateType {
    MANDATORY,    // Принудительное обновление (force)
    OPTIONAL,     // Рекомендованное (можно отложить)
    DEPRECATED;   // Устаревшее (скоро перестанет работать)

    public static UpdateType fromString(String text) {
        for (UpdateType ut : UpdateType.values()) {
            if (ut.name().equalsIgnoreCase(text)) {
                return ut;
            }
        }
        return OPTIONAL;
    }
}