package com.finance.backend.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.service.TransactionService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final TransactionService service;

    public DashboardController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")

    public Map<String, Double> summary(Authentication auth) {
        String userId = auth.getName(); // or map properly later
        return service.getSummary(userId);
    }

    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")

    public Map<String, Double> category(Authentication auth) {
        return service.getCategorySummary(auth.getName());
    }
    
    @GetMapping("/summaryAgg")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")

    public Map<String, Double> summaryAgg(Authentication auth) {
        String userId = auth.getName(); // or map properly later
        return service.getSummary(userId);
    }

    @GetMapping("/categoryAgg")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")

    public Map<String, Double> categoryAgg(Authentication auth) {
        return service.getCategorySummary(auth.getName());
    }
}