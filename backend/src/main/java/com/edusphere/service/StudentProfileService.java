package com.edusphere.service;

import com.edusphere.dto.StudentProfileDTO;
import com.edusphere.entity.StudentProfile;
import com.edusphere.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileDTO getProfileByEmail(String email) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        return convertToDTO(student);
    }

    @Transactional
    public StudentProfileDTO updateProfile(String email, StudentProfileDTO dto) {
        StudentProfile student = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student profile not found for " + email));

        // Editable student fields (phone, gender, dateOfBirth)
        if (dto.getPhone() != null) {
            student.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            student.setGender(dto.getGender());
        }
        if (dto.getDateOfBirth() != null) {
            student.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getLeetcodeUsername() != null) {
            student.setLeetcodeUsername(dto.getLeetcodeUsername());
        }
        if (dto.getGithubUsername() != null) {
            student.setGithubUsername(dto.getGithubUsername());
        }

        // Save profile (Academic attributes like CGPA, Semester, Class, Dept cannot be modified by student!)
        StudentProfile saved = studentProfileRepository.save(student);

        return convertToDTO(saved);
    }

    private StudentProfileDTO convertToDTO(StudentProfile student) {
        return StudentProfileDTO.builder()
                .id(student.getId())
                .registerNumber(student.getRegisterNumber())
                .fullName(student.getUser() != null ? student.getUser().getFullName() : "")
                .email(student.getUser() != null ? student.getUser().getEmail() : "")
                .phone(student.getPhone())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : "")
                .className(student.getCurrentClass() != null ? student.getCurrentClass().getName() : "")
                .currentSemester(student.getCurrentSemester())
                .academicYear(student.getAcademicYear() != null ? student.getAcademicYear().getYearRange() : "")
                .cgpa(student.getCgpa())
                .leetcodeUsername(student.getLeetcodeUsername())
                .githubUsername(student.getGithubUsername())
                .build();
    }
}
