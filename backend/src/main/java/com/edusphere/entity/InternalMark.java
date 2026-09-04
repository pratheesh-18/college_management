package com.edusphere.entity;

import com.edusphere.enums.MarkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internal_marks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarkType markType; // INTERNAL_1, INTERNAL_2, ASSIGNMENT

    private Double marksObtained;

    private Double maxMarks;

    private Integer semester;
}
