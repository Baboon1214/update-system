package com.example.update.repository;

import com.example.update.model.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    Optional<AppVersion> findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(String platform);
}