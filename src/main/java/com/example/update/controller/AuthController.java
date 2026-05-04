package com.example.update.controller;

import com.example.update.dto.AuthRequest;
import com.example.update.dto.AuthResponse;
import com.example.update.model.Role;
import com.example.update.model.User;
import com.example.update.repository.RoleRepository;
import com.example.update.repository.UserRepository;
import com.example.update.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;  // ← теперь бин
    private final JwtUtil jwtUtil;

    // ← ИСПРАВЛЕННЫЙ КОНСТРУКТОР
    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
                BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    
    System.out.println("=== REGISTER ===");
    System.out.println("Username: " + request.getUsername());
    
    if (userRepository.findByUsername(request.getUsername()) != null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User already exists");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(encoder.encode(request.getPassword()));
    user.setEnabled(true);

    // Исправленный блок получения/создания роли USER
    Role userRole = roleRepository.findByName("USER")
            .orElseGet(() -> {
                Role newRole = new Role("USER");
                return roleRepository.save(newRole);
            });
    
    user.setRoles(Set.of(userRole));

    userRepository.save(user);
    System.out.println("User registered!");

    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
}
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        
        User user = userRepository.findByUsername(request.getUsername());
        
        if (user == null || !encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}