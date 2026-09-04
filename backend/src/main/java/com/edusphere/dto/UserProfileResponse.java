package com.edusphere.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
    private String registerNumber;
    private String employeeId;
    private Integer currentSemester;
    private Double cgpa;
    private String departmentCode;
    private String departmentName;
    private String className;
    private String yearRange;
}
