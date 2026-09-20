package com.edusphere.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPlanResponse {
    private String intent;
    private boolean requiresDatabase;
    private String subjectFilter;
    private Integer semesterFilter;
    private List<String> requiredSources;
    private boolean useTextToSql;
    private String sqlQueryHint;
    private String reasoning;
}
