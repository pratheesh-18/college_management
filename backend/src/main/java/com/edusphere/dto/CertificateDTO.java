package com.edusphere.dto;

import com.edusphere.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String registerNumber;
    private String title;
    private String category;
    private String issueOrganization;
    private String fileUrl;
    private VerificationStatus status;
    private String reviewerComments;
    private LocalDateTime uploadedAt;
}
