package com.edusphere.repository;


import com.edusphere.entity.Certificate;
import com.edusphere.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStudentId(Long studentId);
    List<Certificate> findByStudentUserEmail(String email);
    List<Certificate> findByStatus(VerificationStatus status);
}
