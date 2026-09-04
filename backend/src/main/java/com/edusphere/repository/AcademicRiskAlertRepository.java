package com.edusphere.repository;

import com.edusphere.entity.AcademicRiskAlert;
import com.edusphere.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicRiskAlertRepository extends JpaRepository<AcademicRiskAlert, Long> {
    List<AcademicRiskAlert> findByStudentId(Long studentId);
    List<AcademicRiskAlert> findByStudentUserEmail(String email);
    List<AcademicRiskAlert> findByRiskLevel(RiskLevel riskLevel);
    List<AcademicRiskAlert> findByResolvedFalse();
}
