package com.finance.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.backend.dto.*;
import com.finance.backend.entity.User;
import com.finance.backend.repository.UserRepository;
import com.finance.backend.security.jwt.JwtUtil;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(AuthRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));
        repo.save(user);
        System.out.println("Saving user: " + user.getUsername());
        System.out.println("DB save result ID: " + repo.save(user).getId());
        return "Registered";
    }

    public AuthResponse login(AuthRequest request) {
        User user = repo.findByUsername(request.getUsername()).orElseThrow();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return new AuthResponse(jwtUtil.generateToken(user.getUsername()));
    }
    public void assignRole(String username, String role,String currentAdminUsername) {

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate allowed roles
        Set<String> allowedRoles = Set.of("ROLE_ADMIN", "ROLE_ANALYST", "ROLE_VIEWER");

        if (!allowedRoles.contains(role)) {
            throw new RuntimeException("Invalid role");
        }
        
        if (username.equals(currentAdminUsername) && role.equals("ROLE_VIEWER")) {
            throw new RuntimeException("Admin cannot downgrade own role");
        }

        user.setRoles(Set.of(role));
        repo.save(user);
    }
}