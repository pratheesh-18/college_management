package com.edusphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private Set<String> roles;
    private String registerNumber; // For student
    private String employeeId;     // For faculty
    private Long departmentId;
    private Long classId;
    private Long academicYearId;
}
