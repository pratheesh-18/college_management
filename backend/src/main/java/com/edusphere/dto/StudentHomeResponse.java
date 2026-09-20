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
public class StudentHomeResponse {
    private String studentName;
    private String registerNumber;
    private Integer currentSemester;
    private String className;
    private String departmentName;
    private Double cgpa;
    private Double currentGpa;
    private String performanceTrend;
    private List<String> recentActivity;
    private List<String> aiSuggestions;
    private long pendingCertificatesCount;
    private long unreadNotificationsCount;
    private boolean hasRiskAlert;
}
