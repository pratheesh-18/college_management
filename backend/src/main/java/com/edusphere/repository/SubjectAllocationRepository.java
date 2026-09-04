package com.edusphere.repository;

import com.edusphere.entity.SubjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectAllocationRepository extends JpaRepository<SubjectAllocation, Long> {
    List<SubjectAllocation> findByFacultyId(Long facultyId);
    List<SubjectAllocation> findByFacultyUserEmail(String email);
    List<SubjectAllocation> findByClassEntityId(Long classId);
}
