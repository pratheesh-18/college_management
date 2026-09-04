package com.edusphere.controller;

import com.edusphere.dto.AcademicRiskDTO;
import com.edusphere.service.AcademicRiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskAdvisorController {

    private final AcademicRiskService academicRiskService;

    @GetMapping("/alerts/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<AcademicRiskDTO>> getAllAlerts() {
        return ResponseEntity.ok(academicRiskService.getAllRiskAlerts());
    }

    @GetMapping("/alerts/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AcademicRiskDTO>> getMyAlerts(Authentication authentication) {
        return ResponseEntity.ok(academicRiskService.getRiskAlertsByStudentEmail(authentication.getName()));
    }

    @PostMapping("/alerts/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AcademicRiskDTO> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(academicRiskService.resolveRiskAlert(id));
    }
}
