package com.edusphere.ai.service;

import com.edusphere.ai.enums.AiIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiRouterServiceTest {

    private AiRouterService routerService;

    @BeforeEach
    void setUp() {
        routerService = new AiRouterService();
    }

    @Test
    void testGpaIntent() {
        assertEquals(AiIntent.GPA, routerService.detectIntent("What is my semester GPA?"));
        assertEquals(AiIntent.CGPA, routerService.detectIntent("Show my current CGPA score"));
    }

    @Test
    void testInternalMarksIntent() {
        assertEquals(AiIntent.INTERNAL_MARKS, routerService.detectIntent("Show my internal test marks"));
    }

    @Test
    void testWeaknessAnalysisIntent() {
        assertEquals(AiIntent.WEAKNESS_ANALYSIS, routerService.detectIntent("Which subject am I weak in and should focus on?"));
    }

    @Test
    void testSemesterComparisonIntent() {
        assertEquals(AiIntent.PERFORMANCE_COMPARISON, routerService.detectIntent("Compare semester 4 and semester 5 performance"));
    }

    @Test
    void testCertificateIntent() {
        assertEquals(AiIntent.CERTIFICATE_STATUS, routerService.detectIntent("Is my internship certificate verified?"));
        assertEquals(AiIntent.CERTIFICATE_LIST, routerService.detectIntent("Show all my certificates"));
    }

    @Test
    void testLeetCodeIntent() {
        assertEquals(AiIntent.LEETCODE_ANALYSIS, routerService.detectIntent("How is my LeetCode progress and problem solving count?"));
    }

    @Test
    void testGitHubIntent() {
        assertEquals(AiIntent.GITHUB_ANALYSIS, routerService.detectIntent("Analyze my GitHub repositories and commits"));
    }

    @Test
    void testCombinedAnalysisIntent() {
        assertEquals(AiIntent.COMBINED_ACADEMIC_CODING_ANALYSIS, routerService.detectIntent("Analyze my overall academic and coding progress"));
    }
}
