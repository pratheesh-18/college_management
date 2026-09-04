package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AcademicService {

    private final DepartmentRepository departmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectAllocationRepository subjectAllocationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final AcademicRiskAlertRepository riskAlertRepository;

    // Department Management
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    // Academic Year Management
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    public AcademicYear createAcademicYear(AcademicYear academicYear) {
        return academicYearRepository.save(academicYear);
    }

    // Class Management
    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    public ClassEntity createClass(ClassEntity classEntity) {
        return classRepository.save(classEntity);
    }

    // Subject Management
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    // Faculty Allocations
    public List<SubjectAllocation> getAllAllocations() {
        return subjectAllocationRepository.findAll();
    }

    public SubjectAllocation allocateFaculty(Long facultyId, Long subjectId, Long classId) {
        FacultyProfile faculty = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        SubjectAllocation allocation = SubjectAllocation.builder()
                .faculty(faculty)
                .subject(subject)
                .classEntity(classEntity)
                .build();

        return subjectAllocationRepository.save(allocation);
    }

    // Student Promotion
    @Transactional
    public String promoteStudent(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getCurrentSemester() < 8) {
            student.setCurrentSemester(student.getCurrentSemester() + 1);
            studentProfileRepository.save(student);
            return "Student " + student.getUser().getFullName() + " promoted to Semester " + student.getCurrentSemester();
        } else {
            return "Student " + student.getUser().getFullName() + " has already completed Semester 8 (Graduated).";
        }
    }

    // Tutor and Advisor Assignments
    public ClassEntity assignTutor(Long classId, Long facultyId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        FacultyProfile faculty = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        classEntity.setTutor(faculty);
        return classRepository.save(classEntity);
    }

    public ClassEntity assignAdvisor(Long classId, Long facultyId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        FacultyProfile faculty = facultyProfileRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        classEntity.setAdvisor(faculty);
        return classRepository.save(classEntity);
    }

    public List<FacultyProfile> getAllFaculty() {
        return facultyProfileRepository.findAll();
    }

    // Analytics Dashboard Summary
    public Map<String, Object> getSystemAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalStudents", studentProfileRepository.count());
        analytics.put("totalFaculty", facultyProfileRepository.count());
        analytics.put("totalDepartments", departmentRepository.count());
        analytics.put("totalClasses", classRepository.count());
        analytics.put("totalSubjects", subjectRepository.count());
        analytics.put("activeRiskAlerts", riskAlertRepository.findByResolvedFalse().size());
        return analytics;
    }
}
