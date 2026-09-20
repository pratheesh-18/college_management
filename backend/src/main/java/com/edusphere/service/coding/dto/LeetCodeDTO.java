package com.edusphere.service.coding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeetCodeDTO {
    private String username;
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private int totalQuestions;
    private double contestRating;
    private int globalRanking;
    private int recentSubmissionsCount;
    private Map<String, Integer> languageStats;
    private boolean available;
    private String message;
}
