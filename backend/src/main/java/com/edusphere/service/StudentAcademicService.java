package com.edusphere.service;

import com.edusphere.dto.*;
import com.edusphere.entity.*;
import com.edusphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAcademicService {

    private final StudentProfileRepository studentProfileRepository;
    private final InternalMarkRepository internalMarkRepository;
    private final SemesterMarkRepository semesterMarkRepository;
    private final CertificateRepository certificateRepository;
    private final AcademicRiskAlertRepository riskAlertRepository;
    private final NotificationRepository notificationRepository;
    private final AcademicRecordRepository academicRecordRepository;

    public StudentHomeResponse getHomeDashboard(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<SemesterMark> sems = semesterMarkRepository.findByStudentId(student.getId());
        Double currentGpa = calculateSemesterGpa(sems, student.getCurrentSemester());
        long pendingCerts = certificateRepository.findByStudentId(student.getId()).stream()
                .filter(c -> c.getStatus().name().equals("PENDING")).count();

        long unreadNotifs = notificationRepository.countByStudentIdAndIsReadFalse(student.getId());
        boolean hasRisk = !riskAlertRepository.findByStudentIdAndResolvedFalse(student.getId()).isEmpty();

        SemesterComparisonResponse comp = compareSemesters(student);

        List<String> recentActivity = new ArrayList<>();
        recentActivity.add("Logged into EduSphere Student Portal");
        if (hasRisk) {
            recentActivity.add("Academic Risk Advisory Alert active");
        }
        if (pendingCerts > 0) {
            recentActivity.add(pendingCerts + " certificate(s) pending faculty review");
        }
        recentActivity.add("Current Semester " + student.getCurrentSemester() + " GPA: " + String.format("%.2f", currentGpa));

        List<String> aiSuggestions = new ArrayList<>();
        if (currentGpa < 7.5) {
            aiSuggestions.add("Focus on internal test revisions for subjects with lowest scores.");
        } else {
            aiSuggestions.add("Great performance! Target top grades in remaining assessments.");
        }
        aiSuggestions.add("Upload completed NPTEL / Hackathon certificates for academic credit.");

        return StudentHomeResponse.builder()
                .studentName(student.getUser().getFullName())
                .registerNumber(student.getRegisterNumber())
                .currentSemester(student.getCurrentSemester())
                .className(student.getCurrentClass() != null ? student.getCurrentClass().getName() : "Unassigned")
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : "Unassigned")
                .cgpa(student.getCgpa() != null ? student.getCgpa() : 0.0)
                .currentGpa(currentGpa)
                .performanceTrend(comp.getSemesterGpas().isEmpty() ? "STABLE" : comp.getSemesterGpas().get(comp.getSemesterGpas().size() - 1).getPerformanceTrend())
                .recentActivity(recentActivity)
                .aiSuggestions(aiSuggestions)
                .pendingCertificatesCount(pendingCerts)
                .unreadNotificationsCount(unreadNotifs)
                .hasRiskAlert(hasRisk)
                .build();
    }

    public AcademicOverviewResponse getAcademicOverview(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<SemesterMark> sems = semesterMarkRepository.findByStudentId(student.getId());
        List<InternalMark> internals = internalMarkRepository.findByStudentId(student.getId());

        int currentSem = student.getCurrentSemester() != null ? student.getCurrentSemester() : 1;
        Double currentGpa = calculateSemesterGpa(sems, currentSem);
        Double prevGpa = currentSem > 1 ? calculateSemesterGpa(sems, currentSem - 1) : null;

        String trend = "STABLE";
        if (prevGpa != null && prevGpa > 0) {
            if (currentGpa > prevGpa + 0.1) trend = "IMPROVING";
            else if (currentGpa < prevGpa - 0.1) trend = "DECLINED";
        }

        // Subject items
        List<AcademicOverviewResponse.SubjectPerformanceItem> subjectItems = getSubjectPerformanceItems(internals, sems);

        List<AcademicOverviewResponse.SubjectPerformanceItem> strengths = subjectItems.stream()
                .filter(i -> "Strong".equals(i.getStatus()))
                .collect(Collectors.toList());

        List<AcademicOverviewResponse.SubjectPerformanceItem> weaknesses = subjectItems.stream()
                .filter(i -> "Weak".equals(i.getStatus()) || "Needs Attention".equals(i.getStatus()))
                .collect(Collectors.toList());

        long passed = sems.stream().filter(s -> s.getGradePoints() != null && s.getGradePoints() >= 5.0).count();
        long failed = sems.stream().filter(s -> s.getGradePoints() != null && s.getGradePoints() < 5.0).count();

        return AcademicOverviewResponse.builder()
                .studentName(student.getUser().getFullName())
                .registerNumber(student.getRegisterNumber())
                .currentSemester(currentSem)
                .currentGpa(currentGpa)
                .cgpa(student.getCgpa() != null ? student.getCgpa() : 0.0)
                .totalSubjects(subjectItems.size())
                .passedSubjects((int) passed)
                .failedSubjects((int) failed)
                .previousGpa(prevGpa)
                .trend(trend)
                .topStrengths(strengths)
                .weakSubjects(weaknesses)
                .build();
    }

    public List<AcademicRecord> getAcademicHistory(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        return academicRecordRepository.findByStudentIdOrderBySemesterAsc(student.getId());
    }

    public Double calculateGpa(String email, Integer semester) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<SemesterMark> sems = semesterMarkRepository.findByStudentId(student.getId());
        Integer targetSem = semester != null ? semester : student.getCurrentSemester();
        return calculateSemesterGpa(sems, targetSem);
    }

    public Double getCgpa(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        return student.getCgpa() != null ? student.getCgpa() : 0.0;
    }

    public List<InternalMark> getInternalMarks(String email, Integer semester, String examType) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<InternalMark> marks = internalMarkRepository.findByStudentId(student.getId());
        return marks.stream()
                .filter(m -> semester == null || Objects.equals(m.getSemester(), semester))
                .filter(m -> examType == null || m.getMarkType().name().equalsIgnoreCase(examType))
                .collect(Collectors.toList());
    }

    public InternalMarkAnalysisResponse getInternalMarkAnalysis(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<InternalMark> marks = internalMarkRepository.findByStudentId(student.getId());

        Map<Subject, List<InternalMark>> bySubject = marks.stream()
                .collect(Collectors.groupingBy(InternalMark::getSubject));

        List<InternalMarkAnalysisResponse.SubjectInternalAnalysis> analyses = new ArrayList<>();

        for (Map.Entry<Subject, List<InternalMark>> entry : bySubject.entrySet()) {
            Subject subj = entry.getKey();
            List<InternalMark> subMarks = entry.getValue();

            subMarks.sort(Comparator.comparing(InternalMark::getId));

            double highest = subMarks.stream().mapToDouble(InternalMark::getMarksObtained).max().orElse(0.0);
            double lowest = subMarks.stream().mapToDouble(InternalMark::getMarksObtained).min().orElse(0.0);
            double avg = subMarks.stream().mapToDouble(InternalMark::getMarksObtained).average().orElse(0.0);

            double latest = subMarks.get(subMarks.size() - 1).getMarksObtained();
            double prev = subMarks.size() > 1 ? subMarks.get(subMarks.size() - 2).getMarksObtained() : latest;

            double diff = latest - prev;
            String trend = diff > 0 ? "IMPROVING" : diff < 0 ? "DECLINING" : "STABLE";

            List<InternalMarkAnalysisResponse.MarkPoint> points = subMarks.stream()
                    .map(m -> InternalMarkAnalysisResponse.MarkPoint.builder()
                            .examType(m.getMarkType().name())
                            .marksObtained(m.getMarksObtained())
                            .maxMarks(m.getMaxMarks())
                            .build())
                    .collect(Collectors.toList());

            analyses.add(InternalMarkAnalysisResponse.SubjectInternalAnalysis.builder()
                    .subjectId(subj.getId())
                    .subjectCode(subj.getCode())
                    .subjectName(subj.getName())
                    .highestMark(highest)
                    .lowestMark(lowest)
                    .averageMark(Math.round(avg * 10.0) / 10.0)
                    .latestMark(latest)
                    .trend(trend)
                    .markDifference(diff)
                    .markHistory(points)
                    .build());
        }

        return InternalMarkAnalysisResponse.builder()
                .studentName(student.getUser().getFullName())
                .semester(student.getCurrentSemester())
                .subjectAnalyses(analyses)
                .build();
    }

    public List<SemesterMark> getSemesterMarks(String email, Integer semester) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<SemesterMark> sems = semesterMarkRepository.findByStudentId(student.getId());
        return sems.stream()
                .filter(s -> semester == null || Objects.equals(s.getSemester(), semester))
                .collect(Collectors.toList());
    }

    public String getPerformanceTrend(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        SemesterComparisonResponse comp = compareSemesters(student);
        if (comp.getSemesterGpas().isEmpty()) return "INSUFFICIENT_DATA";

        long improving = comp.getSemesterGpas().stream().filter(g -> "IMPROVED".equals(g.getPerformanceTrend())).count();
        long declining = comp.getSemesterGpas().stream().filter(g -> "DECLINED".equals(g.getPerformanceTrend())).count();

        if (improving > declining) return "IMPROVING";
        if (declining > improving) return "DECLINING";
        return "STABLE";
    }

    public SemesterComparisonResponse getSemesterComparison(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        return compareSemesters(student);
    }

    public List<AcademicOverviewResponse.SubjectPerformanceItem> getSubjectPerformance(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        List<InternalMark> internals = internalMarkRepository.findByStudentId(student.getId());
        List<SemesterMark> sems = semesterMarkRepository.findByStudentId(student.getId());

        return getSubjectPerformanceItems(internals, sems);
    }

    private SemesterComparisonResponse compareSemesters(StudentProfile student) {
        List<SemesterMark> allMarks = semesterMarkRepository.findByStudentId(student.getId());

        Map<Integer, List<SemesterMark>> groupedBySem = allMarks.stream()
                .collect(Collectors.groupingBy(SemesterMark::getSemester));

        List<SemesterComparisonResponse.SemesterGpaItem> gpaItems = new ArrayList<>();
        Double previousGpa = null;

        for (int sem = 1; sem <= 8; sem++) {
            List<SemesterMark> marksForSem = groupedBySem.get(sem);
            if (marksForSem != null && !marksForSem.isEmpty()) {
                double totalPoints = 0;
                int totalCredits = 0;
                for (SemesterMark sm : marksForSem) {
                    int cr = (sm.getSubject() != null && sm.getSubject().getCredits() != null) ? sm.getSubject().getCredits() : 3;
                    double gp = sm.getGradePoints() != null ? sm.getGradePoints() : 0.0;
                    totalPoints += gp * cr;
                    totalCredits += cr;
                }
                double gpa = totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;

                String trend = "STABLE";
                if (previousGpa != null) {
                    if (gpa > previousGpa + 0.1) trend = "IMPROVED";
                    else if (gpa < previousGpa - 0.1) trend = "DECLINED";
                }
                previousGpa = gpa;

                gpaItems.add(SemesterComparisonResponse.SemesterGpaItem.builder()
                        .semester(sem)
                        .gpa(gpa)
                        .totalCredits(totalCredits)
                        .totalGradePoints(totalPoints)
                        .performanceTrend(trend)
                        .build());
            }
        }

        return SemesterComparisonResponse.builder()
                .studentId(student.getId())
                .studentName(student.getUser().getFullName())
                .registerNumber(student.getRegisterNumber())
                .currentCgpa(student.getCgpa())
                .semesterGpas(gpaItems)
                .build();
    }

    private List<AcademicOverviewResponse.SubjectPerformanceItem> getSubjectPerformanceItems(List<InternalMark> internals, List<SemesterMark> sems) {
        Map<Subject, List<InternalMark>> bySubj = internals.stream()
                .collect(Collectors.groupingBy(InternalMark::getSubject));

        List<AcademicOverviewResponse.SubjectPerformanceItem> items = new ArrayList<>();

        for (Map.Entry<Subject, List<InternalMark>> entry : bySubj.entrySet()) {
            Subject subj = entry.getKey();
            List<InternalMark> list = entry.getValue();

            double avg = list.stream().mapToDouble(InternalMark::getMarksObtained).average().orElse(0.0);
            double pct = (avg / (list.isEmpty() ? 50.0 : list.get(0).getMaxMarks())) * 100.0;

            String status = pct >= 80.0 ? "Strong" : pct >= 60.0 ? "Average" : "Needs Attention";

            Optional<SemesterMark> semMarkOpt = sems.stream().filter(s -> s.getSubject().getId().equals(subj.getId())).findFirst();
            String grade = semMarkOpt.map(SemesterMark::getLetterGrade).orElse(pct >= 80 ? "A+" : pct >= 60 ? "B+" : "C");

            items.add(AcademicOverviewResponse.SubjectPerformanceItem.builder()
                    .subjectCode(subj.getCode())
                    .subjectName(subj.getName())
                    .averageMark(Math.round(avg * 10.0) / 10.0)
                    .grade(grade)
                    .status(status)
                    .trend(pct >= 80 ? "IMPROVING" : "STABLE")
                    .build());
        }

        return items;
    }

    private Double calculateSemesterGpa(List<SemesterMark> sems, Integer semester) {
        List<SemesterMark> filtered = sems.stream()
                .filter(s -> Objects.equals(s.getSemester(), semester))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;
        for (SemesterMark sm : filtered) {
            int cr = (sm.getSubject() != null && sm.getSubject().getCredits() != null) ? sm.getSubject().getCredits() : 3;
            double gp = sm.getGradePoints() != null ? sm.getGradePoints() : 0.0;
            totalPoints += gp * cr;
            totalCredits += cr;
        }

        return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }
}
