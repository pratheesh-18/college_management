package com.edusphere.service;

import com.edusphere.dto.*;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentReportService {

    private final StudentProfileService profileService;
    private final StudentAcademicService academicService;
    private final CertificateService certificateService;
    private final StudentNotificationService notificationService;

    public AcademicReportDTO getAcademicReport(String email) {
        StudentProfileDTO profile = profileService.getProfileByEmail(email);
        AcademicOverviewResponse overview = academicService.getAcademicOverview(email);
        InternalMarkAnalysisResponse internalAnalysis = academicService.getInternalMarkAnalysis(email);
        SemesterComparisonResponse comparison = academicService.getSemesterComparison(email);
        List<CertificateDTO> certs = certificateService.getCertificatesByStudentEmail(email);

        List<String> aiRecommendations = new ArrayList<>();
        if (overview.getCurrentGpa() >= 8.5) {
            aiRecommendations.add("Outstanding academic performance! Maintain focus on core electives and participate in technical publication.");
        } else if (overview.getCurrentGpa() >= 7.0) {
            aiRecommendations.add("Good progress! Allocate 1 extra hour per week for weak subjects to reach 8.5+ target CGPA.");
        } else {
            aiRecommendations.add("Academic Advisory Alert: Schedule 1-on-1 sessions with subject tutors and complete peer learning modules.");
        }
        aiRecommendations.add("Continue submitting industry certifications to enrich non-academic credits.");

        return AcademicReportDTO.builder()
                .profile(profile)
                .overview(overview)
                .internalMarkAnalyses(internalAnalysis.getSubjectAnalyses())
                .semesterHistory(comparison.getSemesterGpas())
                .strengths(overview.getTopStrengths())
                .weaknesses(overview.getWeakSubjects())
                .certificates(certs)
                .aiRecommendations(aiRecommendations)
                .build();
    }

    public byte[] generateAcademicReportPdf(String email) {
        AcademicReportDTO report = getAcademicReport(email);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            // Title Header
            Paragraph title = new Paragraph("EduSphere AI -- Official Student Academic Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Generated for " + report.getProfile().getFullName() + " | Register No: " + report.getProfile().getRegisterNumber(), subTitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // Student Details Table
            document.add(new Paragraph("1. Student Profile & Credentials", sectionFont));
            PdfPTable profileTable = new PdfPTable(2);
            profileTable.setWidthPercentage(100);
            profileTable.setSpacingBefore(8);
            profileTable.setSpacingAfter(15);

            profileTable.addCell(new Phrase("Student Name: " + report.getProfile().getFullName(), textFont));
            profileTable.addCell(new Phrase("Register Number: " + report.getProfile().getRegisterNumber(), textFont));
            profileTable.addCell(new Phrase("Department: " + report.getProfile().getDepartmentName(), textFont));
            profileTable.addCell(new Phrase("Class / Year: " + report.getProfile().getClassName(), textFont));
            profileTable.addCell(new Phrase("Current Semester: Sem " + report.getProfile().getCurrentSemester(), textFont));
            profileTable.addCell(new Phrase("Cumulative CGPA: " + String.format("%.2f", report.getProfile().getCgpa()), textFont));
            document.add(profileTable);

            // Academic Overview
            document.add(new Paragraph("2. Semester GPA Progression", sectionFont));
            PdfPTable gpaTable = new PdfPTable(4);
            gpaTable.setWidthPercentage(100);
            gpaTable.setSpacingBefore(8);
            gpaTable.setSpacingAfter(15);

            String[] gpaHeaders = {"Semester", "GPA", "Total Credits", "Performance Trend"};
            for (String h : gpaHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(Color.BLUE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                gpaTable.addCell(cell);
            }

            for (SemesterComparisonResponse.SemesterGpaItem item : report.getSemesterHistory()) {
                gpaTable.addCell(new Phrase("Semester " + item.getSemester(), textFont));
                gpaTable.addCell(new Phrase(String.format("%.2f", item.getGpa()), textFont));
                gpaTable.addCell(new Phrase(String.valueOf(item.getTotalCredits()), textFont));
                gpaTable.addCell(new Phrase(item.getPerformanceTrend(), textFont));
            }
            document.add(gpaTable);

            // Internal Marks Breakdown
            document.add(new Paragraph("3. Internal Mark Analysis", sectionFont));
            PdfPTable markTable = new PdfPTable(5);
            markTable.setWidthPercentage(100);
            markTable.setSpacingBefore(8);
            markTable.setSpacingAfter(15);

            String[] markHeaders = {"Subject Code", "Subject Name", "Average", "Latest Test", "Trend"};
            for (String h : markHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(Color.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                markTable.addCell(cell);
            }

            for (InternalMarkAnalysisResponse.SubjectInternalAnalysis sa : report.getInternalMarkAnalyses()) {
                markTable.addCell(new Phrase(sa.getSubjectCode(), textFont));
                markTable.addCell(new Phrase(sa.getSubjectName(), textFont));
                markTable.addCell(new Phrase(String.format("%.1f", sa.getAverageMark()), textFont));
                markTable.addCell(new Phrase(String.format("%.1f", sa.getLatestMark()), textFont));
                markTable.addCell(new Phrase(sa.getTrend(), textFont));
            }
            document.add(markTable);

            // AI Recommendations Section
            document.add(new Paragraph("4. AI Academic Recommendations & Guidance", sectionFont));
            com.lowagie.text.List recList = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
            recList.setIndentationLeft(10);
            for (String rec : report.getAiRecommendations()) {
                recList.add(new ListItem(rec, textFont));
            }
            document.add(recList);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating academic report PDF: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }
}
