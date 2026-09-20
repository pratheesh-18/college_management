package com.edusphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private Long id;
    private String registerNumber;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String departmentName;
    private String className;
    private Integer currentSemester;
    private String academicYear;
    private Double cgpa;
    private String leetcodeUsername;
    private String githubUsername;
}
