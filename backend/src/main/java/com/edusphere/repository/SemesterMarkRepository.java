package com.edusphere.repository;

import com.edusphere.entity.SemesterMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterMarkRepository extends JpaRepository<SemesterMark, Long> {
    List<SemesterMark> findByStudentId(Long studentId);
    List<SemesterMark> findByStudentUserEmail(String email);
    List<SemesterMark> findByStudentIdAndSemester(Long studentId, Integer semester);
}
