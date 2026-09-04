package com.edusphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SemesterMarkRequest {
    private Long studentId;
    private Long subjectId;
    private Integer semester;
    private Double marksObtained;
    private Double maxMarks;
    private Double gradePoints;
    private String letterGrade;
}
