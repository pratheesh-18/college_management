package com.edusphere.controller;

import com.edusphere.dto.SemesterComparisonResponse;
import com.edusphere.dto.StudentMarksSummaryResponse;
import com.edusphere.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
@RequiredArgsConstructor
public class StudentController {

    private final MarkService markService;

    @GetMapping("/marks/summary")
    public ResponseEntity<StudentMarksSummaryResponse> getStudentMarksSummary(Authentication authentication) {
        return ResponseEntity.ok(markService.getStudentMarksSummaryByEmail(authentication.getName()));
    }

    @GetMapping("/performance/compare")
    public ResponseEntity<SemesterComparisonResponse> compareSemesters(Authentication authentication) {
        return ResponseEntity.ok(markService.compareSemesters(authentication.getName()));
    }
}
