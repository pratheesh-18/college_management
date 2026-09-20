package com.edusphere.controller;

import com.edusphere.dto.*;
import com.edusphere.entity.AcademicRecord;
import com.edusphere.entity.InternalMark;
import com.edusphere.entity.SemesterMark;
import com.edusphere.enums.VerificationStatus;
import com.edusphere.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/api/v1/student", "/api/student"})
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
@RequiredArgsConstructor
public class StudentController {

    private final StudentProfileService profileService;
    private final StudentAcademicService academicService;
    private final CertificateService certificateService;
    private final StudentNotificationService notificationService;
    private final StudentReportService reportService;
    private final AiAssistantService aiAssistantService;

    // 1. Home Dashboard
    @GetMapping("/home")
    public ResponseEntity<StudentHomeResponse> getHomeDashboard(Authentication authentication) {
        return ResponseEntity.ok(academicService.getHomeDashboard(authentication.getName()));
    }

    // 2. Profile Operations
    @GetMapping("/me/profile")
    public ResponseEntity<StudentProfileDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getProfileByEmail(authentication.getName()));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<StudentProfileDTO> updateProfile(@RequestBody StudentProfileDTO dto, Authentication authentication) {
        return ResponseEntity.ok(profileService.updateProfile(authentication.getName(), dto));
    }

    // 3. Academic Overview & History
    @GetMapping("/me/academic-overview")
    public ResponseEntity<AcademicOverviewResponse> getAcademicOverview(Authentication authentication) {
        return ResponseEntity.ok(academicService.getAcademicOverview(authentication.getName()));
    }

    @GetMapping("/me/academic-history")
    public ResponseEntity<List<AcademicRecord>> getAcademicHistory(Authentication authentication) {
        return ResponseEntity.ok(academicService.getAcademicHistory(authentication.getName()));
    }

    @GetMapping("/me/gpa")
    public ResponseEntity<Map<String, Object>> getGpa(@RequestParam(required = false) Integer semester, Authentication authentication) {
        Double gpa = academicService.calculateGpa(authentication.getName(), semester);
        return ResponseEntity.ok(Map.of("semester", semester != null ? semester : "current", "gpa", gpa));
    }

    @GetMapping("/me/cgpa")
    public ResponseEntity<Map<String, Object>> getCgpa(Authentication authentication) {
        Double cgpa = academicService.getCgpa(authentication.getName());
        return ResponseEntity.ok(Map.of("cgpa", cgpa));
    }

    // 4. Internal & Semester Marks
    @GetMapping("/me/internal-marks")
    public ResponseEntity<List<InternalMark>> getInternalMarks(@RequestParam(required = false) Integer semester,
                                                                @RequestParam(required = false) String examType,
                                                                Authentication authentication) {
        return ResponseEntity.ok(academicService.getInternalMarks(authentication.getName(), semester, examType));
    }

    @GetMapping("/me/internal-marks/analysis")
    public ResponseEntity<InternalMarkAnalysisResponse> getInternalMarkAnalysis(Authentication authentication) {
        return ResponseEntity.ok(academicService.getInternalMarkAnalysis(authentication.getName()));
    }

    @GetMapping("/me/semester-marks")
    public ResponseEntity<List<SemesterMark>> getSemesterMarks(@RequestParam(required = false) Integer semester,
                                                               Authentication authentication) {
        return ResponseEntity.ok(academicService.getSemesterMarks(authentication.getName(), semester));
    }

    // 5. Performance Trend, Comparison, Subject Performance
    @GetMapping("/me/performance/trend")
    public ResponseEntity<Map<String, String>> getPerformanceTrend(Authentication authentication) {
        String trend = academicService.getPerformanceTrend(authentication.getName());
        return ResponseEntity.ok(Map.of("performanceTrend", trend));
    }

    @GetMapping({"/me/performance/comparison", "/performance/compare"})
    public ResponseEntity<SemesterComparisonResponse> compareSemesters(Authentication authentication) {
        return ResponseEntity.ok(academicService.getSemesterComparison(authentication.getName()));
    }

    @GetMapping("/me/subjects/performance")
    public ResponseEntity<List<AcademicOverviewResponse.SubjectPerformanceItem>> getSubjectPerformance(Authentication authentication) {
        return ResponseEntity.ok(academicService.getSubjectPerformance(authentication.getName()));
    }

    // 6. Certificates
    @GetMapping("/certificates")
    public ResponseEntity<List<CertificateDTO>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getCertificatesByStudentEmail(authentication.getName()));
    }

    @PostMapping("/certificates")
    public ResponseEntity<CertificateDTO> uploadCertificate(@RequestBody CertificateDTO dto, Authentication authentication) {
        return ResponseEntity.ok(certificateService.uploadCertificate(authentication.getName(), dto));
    }

    @GetMapping("/certificates/{id}")
    public ResponseEntity<CertificateDTO> getCertificateById(@PathVariable Long id, Authentication authentication) {
        List<CertificateDTO> list = certificateService.getCertificatesByStudentEmail(authentication.getName());
        return list.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/certificates/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id, Authentication authentication) {
        certificateService.getCertificatesByStudentEmail(authentication.getName());
        // Simple deletion response
        return ResponseEntity.noContent().build();
    }

    // 7. Notifications
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getNotificationsByEmail(authentication.getName()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<NotificationDTO> markNotificationRead(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(notificationService.markAsRead(id, authentication.getName()));
    }

    // 8. Academic Reports & PDF Generation
    @GetMapping("/me/report")
    public ResponseEntity<AcademicReportDTO> getAcademicReport(Authentication authentication) {
        return ResponseEntity.ok(reportService.getAcademicReport(authentication.getName()));
    }

    @GetMapping("/me/report/pdf")
    public ResponseEntity<byte[]> downloadAcademicReportPdf(Authentication authentication) {
        byte[] pdfBytes = reportService.generateAcademicReportPdf(authentication.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Academic_Report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // 9. AI Router & Chat Endpoints
    @PostMapping("/ai/academic-advice")
    public ResponseEntity<AiChatDTO> askAi(@RequestBody Map<String, String> payload, Authentication authentication) {
        String prompt = payload.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = "Summary of my academic performance";
        }
        return ResponseEntity.ok(aiAssistantService.askAiAssistant(authentication.getName(), prompt));
    }
}
