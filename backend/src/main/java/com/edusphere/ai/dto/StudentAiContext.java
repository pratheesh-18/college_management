package com.edusphere.ai.dto;

import com.edusphere.ai.enums.AiIntent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAiContext {
    private String studentName;
    private String registerNumber;
    private String department;
    private String className;
    private Integer currentSemester;
    private Double cgpa;
    private Double currentGpa;
    private Double previousGpa;
    private Double gpaDifference;
    private String performanceTrend;
    private AiIntent intent;

    // Selective detail structures
    private Map<String, Object> academicSummary;
    private List<Map<String, Object>> subjectPerformances;
    private List<Map<String, Object>> internalMarks;
    private List<Map<String, Object>> semesterMarks;
    private List<Map<String, Object>> academicHistory;
    private List<Map<String, Object>> certificates;
    private Map<String, Object> leetCodeStats;
    private Map<String, Object> gitHubStats;
    private List<String> strongSubjects;
    private List<String> weakSubjects;
    private List<String> riskAlerts;
}
