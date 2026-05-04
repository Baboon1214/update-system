package com.example.update.dto;

// Удалите эти импорты
// import lombok.AllArgsConstructor;
// import lombok.Data;

public class AuthResponse {
    private String token;

    // Пустой конструктор (нужен для Jackson)
    public AuthResponse() {}

    // Конструктор с токеном
    public AuthResponse(String token) {
        this.token = token;
    }

    // Геттер
    public String getToken() {
        return token;
    }

    // Сеттер
    public void setToken(String token) {
        this.token = token;
    }
}