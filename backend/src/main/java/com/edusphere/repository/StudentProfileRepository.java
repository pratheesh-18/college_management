package com.edusphere.repository;

import com.edusphere.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserEmail(String email);
    Optional<StudentProfile> findByRegisterNumber(String registerNumber);
    List<StudentProfile> findByCurrentClassId(Long classId);
    List<StudentProfile> findByDepartmentId(Long departmentId);
}
