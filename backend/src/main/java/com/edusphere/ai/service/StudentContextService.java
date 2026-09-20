package com.edusphere.ai.service;

import com.edusphere.ai.dto.AiPlanResponse;
import com.edusphere.ai.dto.StudentAiContext;
import com.edusphere.ai.enums.AiIntent;
import com.edusphere.dto.AcademicOverviewResponse;
import com.edusphere.dto.CertificateDTO;
import com.edusphere.dto.SemesterComparisonResponse;
import com.edusphere.entity.AcademicRecord;
import com.edusphere.entity.AcademicRiskAlert;
import com.edusphere.entity.InternalMark;
import com.edusphere.entity.SemesterMark;
import com.edusphere.entity.StudentProfile;
import com.edusphere.repository.AcademicRiskAlertRepository;
import com.edusphere.repository.StudentProfileRepository;
import com.edusphere.service.CertificateService;
import com.edusphere.service.StudentAcademicService;
import com.edusphere.service.coding.GitHubService;
import com.edusphere.service.coding.LeetCodeService;
import com.edusphere.service.coding.dto.GitHubDTO;
import com.edusphere.service.coding.dto.LeetCodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentContextService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentAcademicService academicService;
    private final CertificateService certificateService;
    private final LeetCodeService leetCodeService;
    private final GitHubService gitHubService;
    private final AcademicAnalysisService academicAnalysisService;
    private final AcademicRiskAlertRepository riskAlertRepository;

    /**
     * Builds a targeted, verified student data context map filtered by Stage 1 LLM plan parameters.
     */
    public Map<String, Object> buildTargetedContext(String email, AiPlanResponse plan) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for email: " + email));

        Map<String, Object> contextMap = new LinkedHashMap<>();

        // Student Profile Header (Always included)
        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("fullName", student.getUser().getFullName());
        studentInfo.put("email", student.getUser().getEmail());
        studentInfo.put("registerNumber", student.getRegisterNumber());
        studentInfo.put("department", student.getDepartment() != null ? student.getDepartment().getName() : "IT");
        studentInfo.put("currentClass", student.getCurrentClass() != null ? student.getCurrentClass().getName() : "Unassigned");
        studentInfo.put("currentSemester", student.getCurrentSemester());
        studentInfo.put("officialCgpa", student.getCgpa());
        contextMap.put("studentProfile", studentInfo);

        if (plan == null || !plan.isRequiresDatabase()) {
            return contextMap;
        }

        String subjectFilter = plan.getSubjectFilter();
        Integer semesterFilter = plan.getSemesterFilter();
        List<String> sources = plan.getRequiredSources() != null ? plan.getRequiredSources() : Collections.emptyList();
        boolean fetchAll = sources.isEmpty() || sources.contains("ALL");

        // 1. Academic Overview & CGPA/GPA
        if (fetchAll || sources.contains("STUDENT_PROFILE") || sources.contains("GPA_CGPA") || sources.contains("ACADEMIC_OVERVIEW")) {
            try {
                AcademicOverviewResponse overview = academicService.getAcademicOverview(email);
                SemesterComparisonResponse comparison = academicService.getSemesterComparison(email);
                Map<String, Object> summary = academicAnalysisService.analyzeAcademicSummary(student, overview, comparison);

                Map<String, Object> gpaData = new LinkedHashMap<>();
                gpaData.put("cumulativeCgpa", overview.getCgpa());
                gpaData.put("currentSemesterGpa", overview.getCurrentGpa());
                gpaData.put("performanceTrend", overview.getTrend());
                gpaData.put("academicSummary", summary);
                contextMap.put("academicOverview", gpaData);
            } catch (Exception e) {
                log.warn("Failed to build overview context: {}", e.getMessage());
            }
        }

        // 2. Internal Marks Breakdown (Targeted by subject/semester filter)
        if (fetchAll || sources.contains("INTERNAL_MARKS")) {
            try {
                List<InternalMark> rawInternals = academicService.getInternalMarks(email, null, null);
                List<Map<String, Object>> filteredInternals = new ArrayList<>();
                for (InternalMark im : rawInternals) {
                    String subName = im.getSubject() != null ? im.getSubject().getName() : "";
                    String subCode = im.getSubject() != null ? im.getSubject().getCode() : "";
                    Integer sem = im.getSemester();

                    if (subjectFilter != null && !subjectFilter.isBlank() &&
                        !subName.toLowerCase().contains(subjectFilter.toLowerCase()) &&
                        !subCode.toLowerCase().contains(subjectFilter.toLowerCase())) {
                        continue;
                    }
                    if (semesterFilter != null && sem != null && !sem.equals(semesterFilter)) {
                        continue;
                    }

                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("subjectName", subName);
                    m.put("subjectCode", subCode);
                    m.put("markType", im.getMarkType() != null ? im.getMarkType().name() : "");
                    m.put("marksObtained", im.getMarksObtained());
                    m.put("maxMarks", im.getMaxMarks());
                    m.put("semester", sem);
                    filteredInternals.add(m);
                }
                contextMap.put("internalMarks", filteredInternals);
            } catch (Exception e) {
                log.warn("Failed to build internal marks context: {}", e.getMessage());
            }
        }

        // 3. Semester Exam Marks (Targeted by subject/semester filter)
        if (fetchAll || sources.contains("SEMESTER_MARKS")) {
            try {
                List<SemesterMark> rawSemesters = academicService.getSemesterMarks(email, null);
                List<Map<String, Object>> filteredSemesters = new ArrayList<>();
                for (SemesterMark sm : rawSemesters) {
                    String subName = sm.getSubject() != null ? sm.getSubject().getName() : "";
                    String subCode = sm.getSubject() != null ? sm.getSubject().getCode() : "";
                    Integer sem = sm.getSemester();

                    if (subjectFilter != null && !subjectFilter.isBlank() &&
                        !subName.toLowerCase().contains(subjectFilter.toLowerCase()) &&
                        !subCode.toLowerCase().contains(subjectFilter.toLowerCase())) {
                        continue;
                    }
                    if (semesterFilter != null && sem != null && !sem.equals(semesterFilter)) {
                        continue;
                    }

                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("subjectName", subName);
                    m.put("subjectCode", subCode);
                    m.put("marksObtained", sm.getMarksObtained());
                    m.put("maxMarks", sm.getMaxMarks());
                    m.put("letterGrade", sm.getLetterGrade());
                    m.put("gradePoints", sm.getGradePoints());
                    m.put("semester", sem);
                    filteredSemesters.add(m);
                }
                contextMap.put("semesterMarks", filteredSemesters);
            } catch (Exception e) {
                log.warn("Failed to build semester marks context: {}", e.getMessage());
            }
        }

        // 4. Academic History & Progression
        if (fetchAll || sources.contains("ACADEMIC_HISTORY")) {
            try {
                List<AcademicRecord> history = academicService.getAcademicHistory(email);
                List<Map<String, Object>> historyList = new ArrayList<>();
                for (AcademicRecord r : history) {
                    Map<String, Object> hm = new LinkedHashMap<>();
                    hm.put("semester", r.getSemester());
                    hm.put("gpa", r.getGpa());
                    hm.put("totalCredits", r.getTotalCredits());
                    hm.put("passedSubjects", r.getPassedSubjects());
                    hm.put("failedSubjects", r.getFailedSubjects());
                    historyList.add(hm);
                }
                contextMap.put("academicHistory", historyList);
            } catch (Exception e) {
                log.warn("Failed to build academic history context: {}", e.getMessage());
            }
        }

        // 5. Certificates & Verification Status
        if (fetchAll || sources.contains("CERTIFICATES")) {
            try {
                List<CertificateDTO> certs = certificateService.getCertificatesByStudentEmail(email);
                List<Map<String, Object>> certList = new ArrayList<>();
                for (CertificateDTO c : certs) {
                    Map<String, Object> cm = new LinkedHashMap<>();
                    cm.put("title", c.getTitle());
                    cm.put("category", c.getCategory());
                    cm.put("issueOrganization", c.getIssueOrganization());
                    cm.put("status", c.getStatus() != null ? c.getStatus().name() : "PENDING");
                    cm.put("reviewerComments", c.getReviewerComments());
                    certList.add(cm);
                }
                contextMap.put("certificates", certList);
            } catch (Exception e) {
                log.warn("Failed to build certificate context: {}", e.getMessage());
            }
        }

        // 6. LeetCode Metrics
        if (fetchAll || sources.contains("LEETCODE")) {
            try {
                LeetCodeDTO lc = leetCodeService.getLeetCodeStats(student.getLeetcodeUsername());
                Map<String, Object> lcMap = new LinkedHashMap<>();
                lcMap.put("username", lc.getUsername());
                lcMap.put("totalSolved", lc.getTotalSolved());
                lcMap.put("easySolved", lc.getEasySolved());
                lcMap.put("mediumSolved", lc.getMediumSolved());
                lcMap.put("hardSolved", lc.getHardSolved());
                lcMap.put("contestRating", lc.getContestRating());
                contextMap.put("leetCodeStats", lcMap);
            } catch (Exception e) {
                log.warn("Failed to fetch LeetCode stats: {}", e.getMessage());
            }
        }

        // 7. GitHub Metrics
        if (fetchAll || sources.contains("GITHUB")) {
            try {
                GitHubDTO gh = gitHubService.getGitHubStats(student.getGithubUsername());
                Map<String, Object> ghMap = new LinkedHashMap<>();
                ghMap.put("username", gh.getUsername());
                ghMap.put("publicRepos", gh.getPublicRepos());
                ghMap.put("followers", gh.getFollowers());
                ghMap.put("totalStars", gh.getTotalStars());
                ghMap.put("recentRepoNames", gh.getRecentRepoNames());
                contextMap.put("gitHubStats", ghMap);
            } catch (Exception e) {
                log.warn("Failed to fetch GitHub stats: {}", e.getMessage());
            }
        }

        // 8. Academic Risk Advisory Alerts
        if (fetchAll || sources.contains("RISK_ALERTS")) {
            try {
                List<AcademicRiskAlert> rawAlerts = riskAlertRepository.findByStudentIdAndResolvedFalse(student.getId());
                List<Map<String, Object>> alerts = new ArrayList<>();
                for (AcademicRiskAlert ra : rawAlerts) {
                    Map<String, Object> am = new LinkedHashMap<>();
                    am.put("riskLevel", ra.getRiskLevel() != null ? ra.getRiskLevel().name() : "HIGH");
                    am.put("reason", ra.getReason());
                    am.put("recommendedAction", ra.getRecommendedAction());
                    alerts.add(am);
                }
                contextMap.put("riskAlerts", alerts);
            } catch (Exception e) {
                log.warn("Failed to fetch risk alerts: {}", e.getMessage());
            }
        }

        return contextMap;
    }

    public StudentAiContext buildContext(String email, AiIntent intent) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for email: " + email));

        AcademicOverviewResponse overview = academicService.getAcademicOverview(email);
        SemesterComparisonResponse comparison = academicService.getSemesterComparison(email);

        Map<String, Object> summary = academicAnalysisService.analyzeAcademicSummary(student, overview, comparison);
        List<String> strongSubjects = academicAnalysisService.extractStrongestSubjects(overview);
        List<String> weakSubjects = academicAnalysisService.extractWeakestSubjects(overview);

        Double effectiveCurrentGpa = overview.getCurrentGpa();
        if ((effectiveCurrentGpa == null || effectiveCurrentGpa == 0.0) && comparison != null && !comparison.getSemesterGpas().isEmpty()) {
            SemesterComparisonResponse.SemesterGpaItem last = comparison.getSemesterGpas().get(comparison.getSemesterGpas().size() - 1);
            effectiveCurrentGpa = last.getGpa();
        }

        List<InternalMark> rawInternals = academicService.getInternalMarks(email, null, null);
        List<Map<String, Object>> internalList = new ArrayList<>();
        for (InternalMark im : rawInternals) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("subjectName", im.getSubject() != null ? im.getSubject().getName() : "Subject");
            m.put("subjectCode", im.getSubject() != null ? im.getSubject().getCode() : "");
            m.put("markType", im.getMarkType() != null ? im.getMarkType().name() : "");
            m.put("marksObtained", im.getMarksObtained());
            m.put("maxMarks", im.getMaxMarks());
            m.put("semester", im.getSemester());
            internalList.add(m);
        }

        List<SemesterMark> rawSemesters = academicService.getSemesterMarks(email, null);
        List<Map<String, Object>> semList = new ArrayList<>();
        for (SemesterMark sm : rawSemesters) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("subjectName", sm.getSubject() != null ? sm.getSubject().getName() : "Subject");
            m.put("subjectCode", sm.getSubject() != null ? sm.getSubject().getCode() : "");
            m.put("marksObtained", sm.getMarksObtained());
            m.put("maxMarks", sm.getMaxMarks());
            m.put("letterGrade", sm.getLetterGrade());
            m.put("gradePoints", sm.getGradePoints());
            m.put("semester", sm.getSemester());
            semList.add(m);
        }

        List<AcademicOverviewResponse.SubjectPerformanceItem> allSubjects = academicService.getSubjectPerformance(email);
        List<Map<String, Object>> subjList = new ArrayList<>();
        for (AcademicOverviewResponse.SubjectPerformanceItem s : allSubjects) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("subjectName", s.getSubjectName());
            m.put("subjectCode", s.getSubjectCode());
            m.put("averageMark", s.getAverageMark());
            m.put("grade", s.getGrade());
            m.put("trend", s.getTrend());
            m.put("status", s.getStatus());
            subjList.add(m);
        }

        List<AcademicRecord> history = academicService.getAcademicHistory(email);
        List<Map<String, Object>> historyList = new ArrayList<>();
        for (AcademicRecord r : history) {
            Map<String, Object> hm = new LinkedHashMap<>();
            hm.put("semester", r.getSemester());
            hm.put("gpa", r.getGpa());
            hm.put("totalCredits", r.getTotalCredits());
            hm.put("passedSubjects", r.getPassedSubjects());
            hm.put("failedSubjects", r.getFailedSubjects());
            historyList.add(hm);
        }

        List<CertificateDTO> certs = certificateService.getCertificatesByStudentEmail(email);
        List<Map<String, Object>> certList = new ArrayList<>();
        for (CertificateDTO c : certs) {
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("title", c.getTitle());
            cm.put("category", c.getCategory());
            cm.put("issueOrganization", c.getIssueOrganization());
            cm.put("status", c.getStatus() != null ? c.getStatus().name() : "PENDING");
            cm.put("reviewerComments", c.getReviewerComments());
            certList.add(cm);
        }

        Map<String, Object> lcMap = new LinkedHashMap<>();
        try {
            LeetCodeDTO lc = leetCodeService.getLeetCodeStats(student.getLeetcodeUsername());
            lcMap.put("username", lc.getUsername());
            lcMap.put("totalSolved", lc.getTotalSolved());
            lcMap.put("easySolved", lc.getEasySolved());
            lcMap.put("mediumSolved", lc.getMediumSolved());
            lcMap.put("hardSolved", lc.getHardSolved());
            lcMap.put("contestRating", lc.getContestRating());
            lcMap.put("globalRanking", lc.getGlobalRanking());
            lcMap.put("languageStats", lc.getLanguageStats());
        } catch (Exception e) {
            log.warn("Failed to fetch LeetCode stats for student context: {}", e.getMessage());
        }

        Map<String, Object> ghMap = new LinkedHashMap<>();
        try {
            GitHubDTO gh = gitHubService.getGitHubStats(student.getGithubUsername());
            ghMap.put("username", gh.getUsername());
            ghMap.put("publicRepos", gh.getPublicRepos());
            ghMap.put("followers", gh.getFollowers());
            ghMap.put("following", gh.getFollowing());
            ghMap.put("totalStars", gh.getTotalStars());
            ghMap.put("recentRepoNames", gh.getRecentRepoNames());
            ghMap.put("languageDistribution", gh.getLanguageDistribution());
        } catch (Exception e) {
            log.warn("Failed to fetch GitHub stats for student context: {}", e.getMessage());
        }

        List<String> riskAlerts = new ArrayList<>();
        try {
            List<AcademicRiskAlert> rawAlerts = riskAlertRepository.findByStudentIdAndResolvedFalse(student.getId());
            for (AcademicRiskAlert ra : rawAlerts) {
                riskAlerts.add("[" + ra.getRiskLevel() + "] " + ra.getReason() + " | Action: " + ra.getRecommendedAction());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch risk alerts for student context: {}", e.getMessage());
        }

        return StudentAiContext.builder()
                .studentName(student.getUser().getFullName())
                .registerNumber(student.getRegisterNumber())
                .department(student.getDepartment() != null ? student.getDepartment().getName() : "IT")
                .className(student.getCurrentClass() != null ? student.getCurrentClass().getName() : "Unassigned")
                .currentSemester(student.getCurrentSemester())
                .cgpa(overview.getCgpa() != null ? overview.getCgpa() : (student.getCgpa() != null ? student.getCgpa() : 0.0))
                .currentGpa(effectiveCurrentGpa)
                .previousGpa(summary.get("previousSemesterGpa") != null ? (Double) summary.get("previousSemesterGpa") : null)
                .gpaDifference((Double) summary.get("gpaDifference"))
                .performanceTrend(overview.getTrend())
                .intent(intent)
                .academicSummary(summary)
                .strongSubjects(strongSubjects)
                .weakSubjects(weakSubjects)
                .internalMarks(internalList)
                .semesterMarks(semList)
                .subjectPerformances(subjList)
                .academicHistory(historyList)
                .certificates(certList)
                .leetCodeStats(lcMap)
                .gitHubStats(ghMap)
                .riskAlerts(riskAlerts)
                .build();
    }
}
