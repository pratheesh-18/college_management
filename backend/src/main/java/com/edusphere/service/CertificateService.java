package com.edusphere.service;

import com.edusphere.dto.CertificateDTO;
import com.edusphere.entity.Certificate;
import com.edusphere.entity.StudentProfile;
import com.edusphere.enums.VerificationStatus;
import com.edusphere.repository.CertificateRepository;
import com.edusphere.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentProfileRepository studentProfileRepository;

    public List<CertificateDTO> getCertificatesByStudentEmail(String email) {
        return certificateRepository.findByStudentUserEmail(email).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CertificateDTO> getAllCertificates() {
        return certificateRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CertificateDTO> getCertificatesByStatus(VerificationStatus status) {
        return certificateRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificateDTO uploadCertificate(String studentEmail, CertificateDTO dto) {
        StudentProfile student = studentProfileRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Certificate certificate = Certificate.builder()
                .student(student)
                .title(dto.getTitle())
                .category(dto.getCategory())
                .issueOrganization(dto.getIssueOrganization())
                .fileUrl(dto.getFileUrl() != null ? dto.getFileUrl() : "https://example.com/certificates/sample.pdf")
                .status(VerificationStatus.PENDING)
                .uploadedAt(LocalDateTime.now())
                .build();

        Certificate saved = certificateRepository.save(certificate);
        return mapToDTO(saved);
    }

    @Transactional
    public CertificateDTO verifyCertificate(Long certificateId, VerificationStatus status, String comments) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificate.setStatus(status);
        certificate.setReviewerComments(comments);

        Certificate saved = certificateRepository.save(certificate);
        return mapToDTO(saved);
    }

    private CertificateDTO mapToDTO(Certificate cert) {
        return CertificateDTO.builder()
                .id(cert.getId())
                .studentId(cert.getStudent().getId())
                .studentName(cert.getStudent().getUser().getFullName())
                .registerNumber(cert.getStudent().getRegisterNumber())
                .title(cert.getTitle())
                .category(cert.getCategory())
                .issueOrganization(cert.getIssueOrganization())
                .fileUrl(cert.getFileUrl())
                .status(cert.getStatus())
                .reviewerComments(cert.getReviewerComments())
                .uploadedAt(cert.getUploadedAt())
                .build();
    }
}
