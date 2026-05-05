package com.example.update.config;

import com.example.update.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

@Override
protected void doFilterInternal(HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {

    String uri = request.getRequestURI();
    if (uri.startsWith("/api/auth/") || uri.startsWith("/swagger-ui/") || uri.startsWith("/v3/api-docs/")) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("JWT_TOKEN".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
    }

    if (token == null) {
        log.debug("No JWT cookie for URI: {}", uri);
        filterChain.doFilter(request, response);
        return;
    }

    if (jwtUtil.validateToken(token)) {
        String username = jwtUtil.extractUsername(token);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("Authenticated user: {} for URI: {}", username, uri);
    } else {
        log.warn("Invalid JWT token in cookie for URI: {}", uri);
    }

    filterChain.doFilter(request, response);
    }
}