package com.edusphere.ai.service;

import com.edusphere.dto.AcademicOverviewResponse;
import com.edusphere.dto.SemesterComparisonResponse;
import com.edusphere.entity.StudentProfile;
import com.edusphere.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AcademicAnalysisServiceTest {

    private AcademicAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new AcademicAnalysisService();
    }

    @Test
    void testAnalyzeAcademicSummaryCalculations() {
        User user = User.builder().fullName("Rahul Verma").build();
        StudentProfile student = StudentProfile.builder()
                .user(user)
                .registerNumber("IT2024001")
                .currentSemester(5)
                .cgpa(8.42)
                .build();

        AcademicOverviewResponse overview = AcademicOverviewResponse.builder()
                .cgpa(8.42)
                .currentGpa(8.60)
                .trend("IMPROVING")
                .totalSubjects(6)
                .passedSubjects(6)
                .failedSubjects(0)
                .build();

        SemesterComparisonResponse comparison = SemesterComparisonResponse.builder()
                .semesterGpas(List.of(
                        SemesterComparisonResponse.SemesterGpaItem.builder().semester(4).gpa(8.00).totalCredits(18).performanceTrend("STABLE").build(),
                        SemesterComparisonResponse.SemesterGpaItem.builder().semester(5).gpa(8.60).totalCredits(18).performanceTrend("IMPROVING").build()
                ))
                .build();

        Map<String, Object> summary = analysisService.analyzeAcademicSummary(student, overview, comparison);

        assertEquals("Rahul Verma", summary.get("studentName"));
        assertEquals(8.42, summary.get("cgpa"));
        assertEquals(8.60, summary.get("currentGpa"));
        assertEquals(8.00, summary.get("previousSemesterGpa"));
        assertEquals(0.60, summary.get("gpaDifference"));
    }

    @Test
    void testExtractStrongAndWeakSubjects() {
        AcademicOverviewResponse overview = AcademicOverviewResponse.builder()
                .topStrengths(List.of(
                        AcademicOverviewResponse.SubjectPerformanceItem.builder().subjectName("DBMS").subjectCode("IT302").averageMark(91.0).status("Strong").grade("O").build()
                ))
                .weakSubjects(List.of(
                        AcademicOverviewResponse.SubjectPerformanceItem.builder().subjectName("Operating Systems").subjectCode("IT305").averageMark(63.0).status("Weak").trend("DECLINING").grade("C").build()
                ))
                .build();

        List<String> strong = analysisService.extractStrongestSubjects(overview);
        List<String> weak = analysisService.extractWeakestSubjects(overview);

        assertEquals(1, strong.size());
        assertTrue(strong.get(0).contains("DBMS"));
        assertEquals(1, weak.size());
        assertTrue(weak.get(0).contains("Operating Systems"));
    }
}
