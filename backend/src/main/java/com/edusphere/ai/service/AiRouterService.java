package com.edusphere.ai.service;

import com.edusphere.ai.enums.AiIntent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class AiRouterService {

    public AiIntent detectIntent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return AiIntent.GENERAL_CHAT;
        }

        String q = query.toLowerCase(Locale.ROOT).trim();

        // 1. Combined Academic + Coding
        if ((q.contains("academic") && q.contains("coding")) ||
            (q.contains("overall") && (q.contains("progress") || q.contains("performance") || q.contains("analysis"))) ||
            (q.contains("improvement plan") || q.contains("personalized plan") || q.contains("everything"))) {
            return AiIntent.COMBINED_ACADEMIC_CODING_ANALYSIS;
        }

        // 2. LeetCode Analysis
        if (q.contains("leetcode") || q.contains("problem solving") || q.contains("dsa practice") || q.contains("coding problem")) {
            return AiIntent.LEETCODE_ANALYSIS;
        }

        // 3. GitHub Analysis
        if (q.contains("github") || q.contains("repository") || q.contains("repositories") || q.contains("git activity") || q.contains("commit")) {
            return AiIntent.GITHUB_ANALYSIS;
        }

        // 4. Combined Coding Profile
        if (q.contains("coding profile") || q.contains("tech stack") || q.contains("technical profile")) {
            return AiIntent.CODING_PROFILE;
        }

        // 5. Certificates
        if (q.contains("certificate") || q.contains("nptel") || q.contains("hackathon") || q.contains("internship certificate") || q.contains("verified") || q.contains("pending certificate")) {
            if (q.contains("pending") || q.contains("approved") || q.contains("status") || q.contains("verified")) {
                return AiIntent.CERTIFICATE_STATUS;
            }
            return AiIntent.CERTIFICATE_LIST;
        }

        // 6. GPA & CGPA
        if (q.contains("cgpa")) {
            return AiIntent.CGPA;
        }
        if (q.contains("gpa") || q.contains("grade point")) {
            return AiIntent.GPA;
        }

        // 7. Internal Marks
        if (q.contains("internal") || q.contains("test mark") || q.contains("internal mark") || q.contains("test 1") || q.contains("test 2")) {
            return AiIntent.INTERNAL_MARKS;
        }

        // 8. Semester Marks
        if (q.contains("semester mark") || q.contains("end sem") || q.contains("board exam")) {
            return AiIntent.SEMESTER_MARKS;
        }

        // 9. Academic History
        if (q.contains("academic history") || q.contains("history") || q.contains("from semester 1") || q.contains("previous years")) {
            return AiIntent.ACADEMIC_HISTORY;
        }

        // 10. Performance Comparison
        if (q.contains("compare") || q.contains("comparison") || q.contains("last two semester") || q.contains("sem 4 and sem 5") || q.contains("semester 4 and semester 5")) {
            return AiIntent.PERFORMANCE_COMPARISON;
        }

        // 11. Performance Trend
        if (q.contains("trend") || q.contains("progression") || q.contains("improving") || q.contains("declining")) {
            return AiIntent.PERFORMANCE_TREND;
        }

        // 12. Subject Performance
        if (q.contains("subject performance") || q.contains("all subjects") || q.contains("marks in java")) {
            return AiIntent.SUBJECT_PERFORMANCE;
        }

        // 13. Strength Analysis
        if (q.contains("strong") || q.contains("strength") || q.contains("best subject") || q.contains("top subject")) {
            return AiIntent.STRENGTH_ANALYSIS;
        }

        // 14. Weakness Analysis
        if (q.contains("weak") || q.contains("weakness") || q.contains("focus on") || q.contains("attention") || q.contains("improve")) {
            return AiIntent.WEAKNESS_ANALYSIS;
        }

        // 15. Recommendation Engine
        if (q.contains("recommend") || q.contains("suggestion") || q.contains("advice") || q.contains("strategy")) {
            return AiIntent.ACADEMIC_RECOMMENDATION;
        }

        // 16. Academic Report
        if (q.contains("report") || q.contains("download pdf") || q.contains("summary report")) {
            return AiIntent.ACADEMIC_REPORT;
        }

        // 17. Academic Overview
        if (q.contains("academic overview") || q.contains("how did i perform") || q.contains("how am i doing") || q.contains("my performance") || q.contains("status")) {
            return AiIntent.ACADEMIC_OVERVIEW;
        }

        // Default General Chat
        return AiIntent.GENERAL_CHAT;
    }
}
