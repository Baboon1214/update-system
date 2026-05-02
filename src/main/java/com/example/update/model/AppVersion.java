package com.example.update.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class AppVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String version;
    private String platform;
    private LocalDateTime releaseDate;

    @Column(length = 1000)
    private String changelog;

    private String updateType;

    private boolean active;

    private boolean forceUpdate;  // Добавляем новое поле
}