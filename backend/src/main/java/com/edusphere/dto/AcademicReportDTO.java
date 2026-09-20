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
public class AcademicReportDTO {
    private StudentProfileDTO profile;
    private AcademicOverviewResponse overview;
    private List<InternalMarkAnalysisResponse.SubjectInternalAnalysis> internalMarkAnalyses;
    private List<SemesterComparisonResponse.SemesterGpaItem> semesterHistory;
    private List<AcademicOverviewResponse.SubjectPerformanceItem> strengths;
    private List<AcademicOverviewResponse.SubjectPerformanceItem> weaknesses;
    private List<CertificateDTO> certificates;
    private List<String> aiRecommendations;
}
