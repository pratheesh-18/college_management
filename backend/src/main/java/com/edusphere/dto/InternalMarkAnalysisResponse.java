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
public class InternalMarkAnalysisResponse {
    private String studentName;
    private Integer semester;
    private List<SubjectInternalAnalysis> subjectAnalyses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectInternalAnalysis {
        private Long subjectId;
        private String subjectCode;
        private String subjectName;
        private Double highestMark;
        private Double lowestMark;
        private Double averageMark;
        private Double latestMark;
        private String trend; // IMPROVING, DECLINING, STABLE
        private Double markDifference; // e.g. +12.0 or -5.0
        private List<MarkPoint> markHistory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarkPoint {
        private String examType;
        private Double marksObtained;
        private Double maxMarks;
    }
}
