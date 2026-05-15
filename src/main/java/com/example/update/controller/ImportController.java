package com.example.update.controller;

import com.example.update.dto.UserImportDTO;
import com.example.update.model.Role;
import com.example.update.model.User;
import com.example.update.model.UserDevice;
import com.example.update.repository.RoleRepository;
import com.example.update.repository.UserDeviceRepository;
import com.example.update.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/import")
@SecurityRequirement(name = "bearerAuth")
public class ImportController {

    private static final Logger log = LoggerFactory.getLogger(ImportController.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDeviceRepository deviceRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ImportController(UserRepository userRepository,
                            RoleRepository roleRepository,
                            UserDeviceRepository deviceRepository,
                            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.deviceRepository = deviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/users", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Загрузить пользователей и их версии из CSV (только для ADMIN)")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        log.info("Importing users from CSV file: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            List<UserImportDTO> importList = new ArrayList<>();
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Пропускаем заголовок
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] columns = line.split(",");
                if (columns.length < 5) {
                    log.warn("Skipping invalid line: {}", line);
                    continue;
                }
                
                UserImportDTO dto = new UserImportDTO();
                dto.setUsername(columns[0].trim());
                dto.setPassword(columns[1].trim());
                dto.setEnabled(Boolean.parseBoolean(columns[2].trim()));
                dto.setPlatform(columns[3].trim());
                dto.setVersion(columns[4].trim());
                
                importList.add(dto);
            }
            
            // Обрабатываем импорт
            int created = 0;
            int updated = 0;
            List<String> errors = new ArrayList<>();
            
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            
            for (UserImportDTO dto : importList) {
                try {
                    User user = userRepository.findByUsername(dto.getUsername());
                    boolean isNewUser = false;
                    
                    if (user == null) {
                        user = new User();
                        user.setUsername(dto.getUsername());
                        user.setPassword(passwordEncoder.encode(dto.getPassword()));
                        user.setEnabled(dto.isEnabled());
                        user.setRoles(Set.of(userRole));
                        userRepository.save(user);
                        isNewUser = true;
                        created++;
                    } else {
                        updated++;
                    }
                    
                    // Обновляем или создаём устройство
                    UserDevice device = deviceRepository.findByUserAndPlatform(user, dto.getPlatform())
                            .orElse(new UserDevice(user, dto.getPlatform(), dto.getVersion()));
                    
                    device.setCurrentVersion(dto.getVersion());
                    device.setLastSeen(java.time.LocalDateTime.now());
                    deviceRepository.save(device);
                    
                    log.info("{} device for user {} on platform {} with version {}", 
                        isNewUser ? "Created" : "Updated", dto.getUsername(), dto.getPlatform(), dto.getVersion());
                    
                } catch (Exception e) {
                    errors.add("Error processing user " + dto.getUsername() + ": " + e.getMessage());
                    log.error("Error processing user: {}", dto.getUsername(), e);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("created", created);
            result.put("updated", updated);
            result.put("errors", errors);
            result.put("total", importList.size());
            
            log.info("Import completed: created={}, updated={}, errors={}", created, updated, errors.size());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to import CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import: " + e.getMessage());
        }
    }
}