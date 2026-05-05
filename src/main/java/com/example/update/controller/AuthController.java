package com.example.update.controller;

import com.example.update.dto.AuthRequest;
import com.example.update.model.Role;
import com.example.update.model.User;
import com.example.update.repository.RoleRepository;
import com.example.update.repository.UserRepository;
import com.example.update.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
        BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()) != null) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role("USER");
                    return roleRepository.save(newRole);
                });

        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        log.info("User registered successfully: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request, HttpServletResponse response) {
    log.info("Login attempt for username: {}", request.getUsername());

    User user = userRepository.findByUsername(request.getUsername());
    if (user == null || !encoder.matches(request.getPassword(), user.getPassword())) {
        log.warn("Login failed - invalid credentials for: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    String token = jwtUtil.generateToken(user.getUsername());

    // Создаём HttpOnly cookie
    Cookie cookie = new Cookie("JWT_TOKEN", token);
    cookie.setHttpOnly(true);          // Недоступен для JavaScript (XSS защита)
    cookie.setSecure(false);           // Для локальной разработки (true для HTTPS)
    cookie.setPath("/");               // Доступен для всех эндпоинтов
    cookie.setMaxAge(24 * 60 * 60);    // 24 часа (соответствует expiration токена)
    response.addCookie(cookie);

    log.info("Login successful for user: {}", request.getUsername());
    // Можно вернуть пустой ответ или сообщение, но тело необязательно
    return ResponseEntity.ok().body("Authenticated");
    }
}