package com.edusphere.controller;

import com.edusphere.dto.CertificateDTO;
import com.edusphere.enums.VerificationStatus;
import com.edusphere.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CertificateDTO>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getCertificatesByStudentEmail(authentication.getName()));
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CertificateDTO> uploadCertificate(@RequestBody CertificateDTO dto, Authentication authentication) {
        return ResponseEntity.ok(certificateService.uploadCertificate(authentication.getName(), dto));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<List<CertificateDTO>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<CertificateDTO> verifyCertificate(@PathVariable Long id,
                                                             @RequestParam VerificationStatus status,
                                                             @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(certificateService.verifyCertificate(id, status, comments));
    }
}
