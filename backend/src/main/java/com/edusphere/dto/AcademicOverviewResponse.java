package com.edusphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicOverviewResponse {
    private String studentName;
    private String registerNumber;
    private Integer currentSemester;
    private Double currentGpa;
    private Double cgpa;
    private Integer totalSubjects;
    private Integer passedSubjects;
    private Integer failedSubjects;
    private Double previousGpa;
    private String trend; // "IMPROVING", "DECLINING", "STABLE"
    private List<SubjectPerformanceItem> topStrengths;
    private List<SubjectPerformanceItem> weakSubjects;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPerformanceItem {
        private String subjectCode;
        private String subjectName;
        private Double averageMark;
        private String grade;
        private String status; // "Strong", "Average", "Weak"
        private String trend;
    }
}
