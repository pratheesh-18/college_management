package com.edusphere.repository;

import com.edusphere.entity.FacultyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyProfileRepository extends JpaRepository<FacultyProfile, Long> {
    Optional<FacultyProfile> findByUserEmail(String email);
    Optional<FacultyProfile> findByEmployeeId(String employeeId);
}
