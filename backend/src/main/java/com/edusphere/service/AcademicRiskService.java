package com.edusphere.service;

import com.edusphere.dto.AcademicRiskDTO;
import com.edusphere.entity.*;
import com.edusphere.enums.RiskLevel;
import com.edusphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicRiskService {

    private final AcademicRiskAlertRepository riskAlertRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InternalMarkRepository internalMarkRepository;
    private final SemesterMarkRepository semesterMarkRepository;

    @Transactional
    public List<AcademicRiskAlert> evaluateStudentRisk(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId).orElse(null);
        if (student == null) return new ArrayList<>();

        List<AcademicRiskAlert> generatedAlerts = new ArrayList<>();

        // Rule 1: Low CGPA Check
        if (student.getCgpa() != null && student.getCgpa() < 6.0) {
            RiskLevel level = student.getCgpa() < 5.0 ? RiskLevel.CRITICAL : RiskLevel.HIGH;
            String reason = "Overall CGPA is " + student.getCgpa() + " (below 6.0 threshold).";
            String action = "Assign academic mentor for weekly progress monitoring and remedial coaching.";

            createOrUpdateAlert(student, level, reason, action, generatedAlerts);
        } else if (student.getCgpa() != null && student.getCgpa() < 7.0) {
            String reason = "CGPA is " + student.getCgpa() + " (near border threshold).";
            String action = "Encourage participation in peer study groups and core subject revision.";

            createOrUpdateAlert(student, RiskLevel.MEDIUM, reason, action, generatedAlerts);
        }

        // Rule 2: Low Internal Marks Check (<40%)
        List<InternalMark> internals = internalMarkRepository.findByStudentId(studentId);
        for (InternalMark mark : internals) {
            if (mark.getMarksObtained() != null && mark.getMaxMarks() != null && mark.getMaxMarks() > 0) {
                double percentage = (mark.getMarksObtained() / mark.getMaxMarks()) * 100.0;
                if (percentage < 40.0) {
                    String subName = mark.getSubject() != null ? mark.getSubject().getName() : "Subject";
                    String reason = "Scored " + mark.getMarksObtained() + "/" + mark.getMaxMarks() + " (" + String.format("%.1f", percentage) + "%) in " + mark.getMarkType() + " for " + subName;
                    String action = "Schedule special tutorial sessions and practice problem sets for " + subName;

                    createOrUpdateAlert(student, RiskLevel.HIGH, reason, action, generatedAlerts);
                }
            }
        }

        return generatedAlerts;
    }

    private void createOrUpdateAlert(StudentProfile student, RiskLevel level, String reason, String action, List<AcademicRiskAlert> list) {
        List<AcademicRiskAlert> existing = riskAlertRepository.findByStudentId(student.getId());
        boolean exists = existing.stream().anyMatch(a -> a.getReason().equalsIgnoreCase(reason));
        if (!exists) {
            AcademicRiskAlert alert = AcademicRiskAlert.builder()
                    .student(student)
                    .riskLevel(level)
                    .reason(reason)
                    .recommendedAction(action)
                    .createdAt(LocalDateTime.now())
                    .resolved(false)
                    .build();
            list.add(riskAlertRepository.save(alert));
        }
    }

    public List<AcademicRiskDTO> getAllRiskAlerts() {
        return riskAlertRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AcademicRiskDTO> getRiskAlertsByStudentEmail(String email) {
        return riskAlertRepository.findByStudentUserEmail(email).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AcademicRiskDTO resolveRiskAlert(Long alertId) {
        AcademicRiskAlert alert = riskAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setResolved(true);
        return mapToDTO(riskAlertRepository.save(alert));
    }

    private AcademicRiskDTO mapToDTO(AcademicRiskAlert alert) {
        return AcademicRiskDTO.builder()
                .id(alert.getId())
                .studentId(alert.getStudent().getId())
                .studentName(alert.getStudent().getUser().getFullName())
                .registerNumber(alert.getStudent().getRegisterNumber())
                .className(alert.getStudent().getCurrentClass() != null ? alert.getStudent().getCurrentClass().getName() : "N/A")
                .cgpa(alert.getStudent().getCgpa())
                .riskLevel(alert.getRiskLevel())
                .reason(alert.getReason())
                .recommendedAction(alert.getRecommendedAction())
                .createdAt(alert.getCreatedAt())
                .resolved(alert.getResolved())
                .build();
    }
}
