package com.edusphere.dto;

import com.edusphere.enums.MarkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InternalMarkRequest {
    private Long studentId;
    private Long subjectId;
    private MarkType markType;
    private Double marksObtained;
    private Double maxMarks;
    private Integer semester;
}
