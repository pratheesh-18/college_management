package com.edusphere.service.coding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubDTO {
    private String username;
    private int publicRepos;
    private int followers;
    private int following;
    private int totalStars;
    private Map<String, Integer> languageDistribution;
    private List<String> recentRepoNames;
    private int recentContributions;
    private boolean available;
    private String message;
}
