package com.edusphere.ai.service;

import com.edusphere.dto.AcademicOverviewResponse;
import com.edusphere.dto.InternalMarkAnalysisResponse;
import com.edusphere.dto.SemesterComparisonResponse;
import com.edusphere.entity.InternalMark;
import com.edusphere.entity.SemesterMark;
import com.edusphere.entity.StudentProfile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcademicAnalysisService {

    public Map<String, Object> analyzeAcademicSummary(StudentProfile student, AcademicOverviewResponse overview, SemesterComparisonResponse comparison) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("studentName", student.getUser().getFullName());
        summary.put("registerNumber", student.getRegisterNumber());
        summary.put("department", student.getDepartment() != null ? student.getDepartment().getName() : "IT");
        summary.put("className", student.getCurrentClass() != null ? student.getCurrentClass().getName() : "Unassigned");
        summary.put("currentSemester", student.getCurrentSemester());

        double cgpa = overview.getCgpa() != null ? overview.getCgpa() : (student.getCgpa() != null ? student.getCgpa() : 0.0);
        summary.put("cgpa", Math.round(cgpa * 100.0) / 100.0);
        summary.put("currentGpa", Math.round(overview.getCurrentGpa() * 100.0) / 100.0);
        summary.put("trend", overview.getTrend() != null ? overview.getTrend() : "STABLE");

        if (comparison != null && comparison.getSemesterGpas() != null && comparison.getSemesterGpas().size() >= 2) {
            List<SemesterComparisonResponse.SemesterGpaItem> items = comparison.getSemesterGpas();
            SemesterComparisonResponse.SemesterGpaItem curr = items.get(items.size() - 1);
            SemesterComparisonResponse.SemesterGpaItem prev = items.get(items.size() - 2);
            double diff = curr.getGpa() - prev.getGpa();
            summary.put("previousSemesterGpa", Math.round(prev.getGpa() * 100.0) / 100.0);
            summary.put("gpaDifference", Math.round(diff * 100.0) / 100.0);
            summary.put("percentageChange", Math.round((diff / (prev.getGpa() == 0 ? 1 : prev.getGpa()) * 100.0) * 10.0) / 10.0);
        } else {
            summary.put("previousSemesterGpa", null);
            summary.put("gpaDifference", 0.0);
            summary.put("percentageChange", 0.0);
        }

        summary.put("totalSubjectsTracked", overview.getTotalSubjects());
        summary.put("passedSubjects", overview.getPassedSubjects());
        summary.put("failedSubjects", overview.getFailedSubjects());

        return summary;
    }

    public List<String> extractStrongestSubjects(AcademicOverviewResponse overview) {
        if (overview.getTopStrengths() == null) return Collections.emptyList();

        return overview.getTopStrengths().stream()
                .map(s -> String.format("%s (%s: %.1f%%)", s.getSubjectName(), s.getSubjectCode(), s.getAverageMark()))
                .collect(Collectors.toList());
    }

    public List<String> extractWeakestSubjects(AcademicOverviewResponse overview) {
        if (overview.getWeakSubjects() == null) return Collections.emptyList();

        return overview.getWeakSubjects().stream()
                .map(s -> String.format("%s (%s: %.1f%% - Trend: %s)", s.getSubjectName(), s.getSubjectCode(), s.getAverageMark(), s.getTrend()))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> formatInternalMarksAnalysis(InternalMarkAnalysisResponse analysis) {
        if (analysis == null || analysis.getSubjectAnalyses() == null) return Collections.emptyList();

        List<Map<String, Object>> list = new ArrayList<>();
        for (InternalMarkAnalysisResponse.SubjectInternalAnalysis sa : analysis.getSubjectAnalyses()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("subjectName", sa.getSubjectName());
            map.put("subjectCode", sa.getSubjectCode());
            map.put("latestMark", sa.getLatestMark());
            map.put("averageMark", sa.getAverageMark());
            map.put("trend", sa.getTrend());
            list.add(map);
        }
        return list;
    }
}
