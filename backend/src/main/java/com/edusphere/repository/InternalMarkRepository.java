package com.edusphere.repository;

import com.edusphere.entity.InternalMark;
import com.edusphere.enums.MarkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalMarkRepository extends JpaRepository<InternalMark, Long> {
    List<InternalMark> findByStudentId(Long studentId);
    List<InternalMark> findByStudentUserEmail(String email);
    List<InternalMark> findBySubjectIdAndSemester(Long subjectId, Integer semester);
    List<InternalMark> findByStudentIdAndSubjectIdAndMarkType(Long studentId, Long subjectId, MarkType markType);
}
