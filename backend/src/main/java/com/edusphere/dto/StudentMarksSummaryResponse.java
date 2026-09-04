package com.edusphere.dto;

import com.edusphere.entity.InternalMark;
import com.edusphere.entity.SemesterMark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentMarksSummaryResponse {
    private Long studentId;
    private String studentName;
    private String registerNumber;
    private Double cgpa;
    private Double currentGpa;
    private List<InternalMark> internalMarks;
    private List<SemesterMark> semesterMarks;
}
