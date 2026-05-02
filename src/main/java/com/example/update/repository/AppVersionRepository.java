package com.example.update.repository;

import com.example.update.model.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

  AppVersion findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(String platform);
}