package com.edusphere.service;

import com.edusphere.dto.*;
import com.edusphere.entity.*;
import com.edusphere.enums.MarkType;
import com.edusphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkService {

    private final InternalMarkRepository internalMarkRepository;
    private final SemesterMarkRepository semesterMarkRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectAllocationRepository subjectAllocationRepository;
    private final AcademicRiskService academicRiskService;

    @Transactional
    public InternalMark saveInternalMark(InternalMarkRequest request) {
        StudentProfile student = studentProfileRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<InternalMark> existingList = internalMarkRepository
                .findByStudentIdAndSubjectIdAndMarkType(student.getId(), subject.getId(), request.getMarkType());

        InternalMark mark;
        if (!existingList.isEmpty()) {
            mark = existingList.get(0);
            mark.setMarksObtained(request.getMarksObtained());
            mark.setMaxMarks(request.getMaxMarks());
            mark.setSemester(request.getSemester() != null ? request.getSemester() : student.getCurrentSemester());
        } else {
            mark = InternalMark.builder()
                    .student(student)
                    .subject(subject)
                    .markType(request.getMarkType())
                    .marksObtained(request.getMarksObtained())
                    .maxMarks(request.getMaxMarks())
                    .semester(request.getSemester() != null ? request.getSemester() : student.getCurrentSemester())
                    .build();
        }

        InternalMark saved = internalMarkRepository.save(mark);
        
        // Trigger automated risk evaluation
        academicRiskService.evaluateStudentRisk(student.getId());

        return saved;
    }

    @Transactional
    public SemesterMark saveSemesterMark(SemesterMarkRequest request) {
        StudentProfile student = studentProfileRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        SemesterMark mark = SemesterMark.builder()
                .student(student)
                .subject(subject)
                .semester(request.getSemester())
                .marksObtained(request.getMarksObtained())
                .maxMarks(request.getMaxMarks())
                .gradePoints(request.getGradePoints())
                .letterGrade(request.getLetterGrade())
                .build();

        SemesterMark saved = semesterMarkRepository.save(mark);

        // Recalculate CGPA for Student
        recalculateStudentCgpa(student.getId());
        academicRiskService.evaluateStudentRisk(student.getId());

        return saved;
    }

    @Transactional
    public void recalculateStudentCgpa(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId).orElse(null);
        if (student == null) return;

        List<SemesterMark> semesterMarks = semesterMarkRepository.findByStudentId(studentId);
        if (semesterMarks.isEmpty()) return;

        double totalWeightedGradePoints = 0.0;
        int totalCredits = 0;

        for (SemesterMark sm : semesterMarks) {
            int credits = (sm.getSubject() != null && sm.getSubject().getCredits() != null) 
                    ? sm.getSubject().getCredits() : 3;
            double gp = sm.getGradePoints() != null ? sm.getGradePoints() : 0.0;
            totalWeightedGradePoints += gp * credits;
            totalCredits += credits;
        }

        if (totalCredits > 0) {
            double cgpa = Math.round((totalWeightedGradePoints / totalCredits) * 100.0) / 100.0;
            student.setCgpa(cgpa);
            studentProfileRepository.save(student);
        }
    }

    public StudentMarksSummaryResponse getStudentMarksSummaryByEmail(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<InternalMark> internals = internalMarkRepository.findByStudentId(student.getId());
        List<SemesterMark> sems = semesterMarkRepository.findByStudentId(student.getId());

        Double currentGpa = calculateSemesterGpa(sems, student.getCurrentSemester());

        return StudentMarksSummaryResponse.builder()
                .studentId(student.getId())
                .studentName(student.getUser().getFullName())
                .registerNumber(student.getRegisterNumber())
                .cgpa(student.getCgpa())
                .currentGpa(currentGpa)
                .internalMarks(internals)
                .semesterMarks(sems)
                .build();
    }

    public SemesterComparisonResponse compareSemesters(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        List<SemesterMark> allMarks = semesterMarkRepository.findByStudentId(student.getId());

        Map<Integer, List<SemesterMark>> groupedBySem = allMarks.stream()
                .collect(Collectors.groupingBy(SemesterMark::getSemester));

        List<SemesterComparisonResponse.SemesterGpaItem> gpaItems = new ArrayList<>();
        Double previousGpa = null;

        for (int sem = 1; sem <= 8; sem++) {
            List<SemesterMark> marksForSem = groupedBySem.get(sem);
            if (marksForSem != null && !marksForSem.isEmpty()) {
                double totalPoints = 0;
                int totalCredits = 0;
                for (SemesterMark sm : marksForSem) {
                    int cr = (sm.getSubject() != null && sm.getSubject().getCredits() != null) ? sm.getSubject().getCredits() : 3;
                    double gp = sm.getGradePoints() != null ? sm.getGradePoints() : 0.0;
                    totalPoints += gp * cr;
                    totalCredits += cr;
                }
                double gpa = totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;

                String trend = "STABLE";
                if (previousGpa != null) {
                    if (gpa > previousGpa + 0.1) trend = "IMPROVED";
                    else if (gpa < previousGpa - 0.1) trend = "DECLINED";
                }
                previousGpa = gpa;

                gpaItems.add(SemesterComparisonResponse.SemesterGpaItem.builder()
                        .semester(sem)
                        .gpa(gpa)
                        .totalCredits(totalCredits)
                        .totalGradePoints(totalPoints)
                        .performanceTrend(trend)
                        .build());
            }
        }

        return SemesterComparisonResponse.builder()
                .studentId(student.getId())
                .studentName(student.getUser().getFullName())
                .registerNumber(student.getRegisterNumber())
                .currentCgpa(student.getCgpa())
                .semesterGpas(gpaItems)
                .build();
    }

    public List<SubjectAllocation> getFacultyAssignedSubjects(String facultyEmail) {
        return subjectAllocationRepository.findByFacultyUserEmail(facultyEmail);
    }

    public List<StudentProfile> getStudentsInClass(Long classId) {
        return studentProfileRepository.findByCurrentClassId(classId);
    }

    private Double calculateSemesterGpa(List<SemesterMark> sems, Integer semester) {
        List<SemesterMark> filtered = sems.stream()
                .filter(s -> Objects.equals(s.getSemester(), semester))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;
        for (SemesterMark sm : filtered) {
            int cr = (sm.getSubject() != null && sm.getSubject().getCredits() != null) ? sm.getSubject().getCredits() : 3;
            double gp = sm.getGradePoints() != null ? sm.getGradePoints() : 0.0;
            totalPoints += gp * cr;
            totalCredits += cr;
        }

        return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }
}
