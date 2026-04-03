package com.finance.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.finance.backend.dto.RoleUpdateRequest;
import com.finance.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/data")
    @PreAuthorize("hasRole('ADMIN')")
    public String data() {
        return "Permission secured data";
    }

    // 🔥 NEW API
    @PutMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignRole(@RequestBody @Valid RoleUpdateRequest request,Authentication auth) {

        authService.assignRole(request.getUsername(), request.getRole(),auth.getName());

        return "Role updated successfully";
    }
}