package com.edusphere.controller;

import com.edusphere.entity.*;
import com.edusphere.service.AcademicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AcademicService academicService;

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(academicService.getSystemAnalytics());
    }

    // Departments
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(academicService.getAllDepartments());
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        return ResponseEntity.ok(academicService.createDepartment(department));
    }

    // Academic Years
    @GetMapping("/academic-years")
    public ResponseEntity<List<AcademicYear>> getAcademicYears() {
        return ResponseEntity.ok(academicService.getAllAcademicYears());
    }

    @PostMapping("/academic-years")
    public ResponseEntity<AcademicYear> createAcademicYear(@RequestBody AcademicYear academicYear) {
        return ResponseEntity.ok(academicService.createAcademicYear(academicYear));
    }

    // Classes
    @GetMapping("/classes")
    public ResponseEntity<List<ClassEntity>> getClasses() {
        return ResponseEntity.ok(academicService.getAllClasses());
    }

    @PostMapping("/classes")
    public ResponseEntity<ClassEntity> createClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.ok(academicService.createClass(classEntity));
    }

    // Subjects
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects() {
        return ResponseEntity.ok(academicService.getAllSubjects());
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.ok(academicService.createSubject(subject));
    }

    // Allocations
    @GetMapping("/allocations")
    public ResponseEntity<List<SubjectAllocation>> getAllocations() {
        return ResponseEntity.ok(academicService.getAllAllocations());
    }

    @PostMapping("/allocations")
    public ResponseEntity<SubjectAllocation> allocateFaculty(@RequestParam Long facultyId,
                                                               @RequestParam Long subjectId,
                                                               @RequestParam Long classId) {
        return ResponseEntity.ok(academicService.allocateFaculty(facultyId, subjectId, classId));
    }

    // Faculty
    @GetMapping("/faculty")
    public ResponseEntity<List<FacultyProfile>> getFaculty() {
        return ResponseEntity.ok(academicService.getAllFaculty());
    }

    // Tutor & Advisor Assignments
    @PostMapping("/tutor/assign")
    public ResponseEntity<ClassEntity> assignTutor(@RequestParam Long classId, @RequestParam Long facultyId) {
        return ResponseEntity.ok(academicService.assignTutor(classId, facultyId));
    }

    @PostMapping("/advisor/assign")
    public ResponseEntity<ClassEntity> assignAdvisor(@RequestParam Long classId, @RequestParam Long facultyId) {
        return ResponseEntity.ok(academicService.assignAdvisor(classId, facultyId));
    }

    // Student Promotion
    @PostMapping("/students/{studentId}/promote")
    public ResponseEntity<String> promoteStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicService.promoteStudent(studentId));
    }
}
