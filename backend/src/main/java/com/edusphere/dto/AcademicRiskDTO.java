package com.edusphere.dto;

import com.edusphere.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcademicRiskDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String registerNumber;
    private String className;
    private Double cgpa;
    private RiskLevel riskLevel;
    private String reason;
    private String recommendedAction;
    private LocalDateTime createdAt;
    private Boolean resolved;
}
