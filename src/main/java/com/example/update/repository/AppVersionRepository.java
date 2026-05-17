package com.example.update.repository;

import com.example.update.model.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
    
    Optional<AppVersion> findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(String platform);
    
    Optional<AppVersion> findByPlatformAndVersion(String platform, String version);

    Optional<AppVersion> findTopByOrderByReleaseDateDesc();
    
    List<AppVersion> findByPlatform(String platform);
    
    List<AppVersion> findByActiveTrue();
    
    List<AppVersion> findByPlatformAndActiveTrue(String platform);
    
    Optional<AppVersion> findTopByPlatformOrderByReleaseDateDesc(String platform);
}