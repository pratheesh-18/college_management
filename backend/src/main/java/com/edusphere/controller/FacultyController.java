package com.edusphere.controller;

import com.edusphere.dto.InternalMarkRequest;
import com.edusphere.dto.SemesterMarkRequest;
import com.edusphere.entity.InternalMark;
import com.edusphere.entity.SemesterMark;
import com.edusphere.entity.StudentProfile;
import com.edusphere.entity.SubjectAllocation;
import com.edusphere.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/faculty")
@PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
@RequiredArgsConstructor
public class FacultyController {

    private final MarkService markService;

    @GetMapping("/assigned-subjects")
    public ResponseEntity<List<SubjectAllocation>> getAssignedSubjects(Authentication authentication) {
        return ResponseEntity.ok(markService.getFacultyAssignedSubjects(authentication.getName()));
    }

    @GetMapping("/classes/{classId}/students")
    public ResponseEntity<List<StudentProfile>> getStudentsInClass(@PathVariable Long classId) {
        return ResponseEntity.ok(markService.getStudentsInClass(classId));
    }

    @PostMapping("/marks/internal")
    public ResponseEntity<InternalMark> saveInternalMark(@RequestBody InternalMarkRequest request) {
        return ResponseEntity.ok(markService.saveInternalMark(request));
    }

    @PostMapping("/marks/semester")
    public ResponseEntity<SemesterMark> saveSemesterMark(@RequestBody SemesterMarkRequest request) {
        return ResponseEntity.ok(markService.saveSemesterMark(request));
    }
}
