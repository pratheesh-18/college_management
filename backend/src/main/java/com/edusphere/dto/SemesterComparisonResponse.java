package com.edusphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SemesterComparisonResponse {
    private Long studentId;
    private String studentName;
    private String registerNumber;
    private Double currentCgpa;
    private List<SemesterGpaItem> semesterGpas;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SemesterGpaItem {
        private Integer semester;
        private Double gpa;
        private Integer totalCredits;
        private Double totalGradePoints;
        private String performanceTrend; // IMPROVED, STABLE, DECLINED
    }
}
