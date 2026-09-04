package com.edusphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "semester_marks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private Integer semester;

    private Double gradePoints; // e.g. 10.0 for O, 9.0 for A+, 8.0 for A, 7.0 for B+, etc.

    private String letterGrade; // O, A+, A, B+, B, C, F

    private Double marksObtained;

    private Double maxMarks;
}
