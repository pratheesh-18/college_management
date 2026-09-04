package com.edusphere.service;

import com.edusphere.dto.JwtResponse;
import com.edusphere.dto.LoginRequest;
import com.edusphere.dto.RegisterRequest;
import com.edusphere.dto.UserProfileResponse;
import com.edusphere.entity.*;
import com.edusphere.enums.RoleType;
import com.edusphere.repository.*;
import com.edusphere.security.CustomUserDetails;
import com.edusphere.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ClassRepository classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        Long profileId = null;
        String departmentName = null;
        String className = null;

        if (roles.contains("ROLE_STUDENT")) {
            var studentOpt = studentProfileRepository.findByUserEmail(userDetails.getEmail());
            if (studentOpt.isPresent()) {
                profileId = studentOpt.get().getId();
                if (studentOpt.get().getDepartment() != null) {
                    departmentName = studentOpt.get().getDepartment().getName();
                }
                if (studentOpt.get().getCurrentClass() != null) {
                    className = studentOpt.get().getCurrentClass().getName();
                }
            }
        } else if (roles.contains("ROLE_FACULTY")) {
            var facultyOpt = facultyProfileRepository.findByUserEmail(userDetails.getEmail());
            if (facultyOpt.isPresent()) {
                profileId = facultyOpt.get().getId();
                if (facultyOpt.get().getDepartment() != null) {
                    departmentName = facultyOpt.get().getDepartment().getName();
                }
            }
        }

        return JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .roles(roles)
                .profileId(profileId)
                .departmentName(departmentName)
                .className(className)
                .build();
    }

    @Transactional
    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .roles(new HashSet<>())
                .build();

        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "faculty":
                        Role facultyRole = roleRepository.findByName(RoleType.ROLE_FACULTY)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(facultyRole);
                        break;
                    default:
                        Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(studentRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Profile Creation
        if (roles.stream().anyMatch(r -> r.getName() == RoleType.ROLE_STUDENT)) {
            StudentProfile studentProfile = StudentProfile.builder()
                    .user(savedUser)
                    .registerNumber(registerRequest.getRegisterNumber() != null ? registerRequest.getRegisterNumber() : "REG" + System.currentTimeMillis())
                    .currentSemester(1)
                    .cgpa(0.0)
                    .build();

            if (registerRequest.getDepartmentId() != null) {
                departmentRepository.findById(registerRequest.getDepartmentId()).ifPresent(studentProfile::setDepartment);
            }
            if (registerRequest.getClassId() != null) {
                classRepository.findById(registerRequest.getClassId()).ifPresent(studentProfile::setCurrentClass);
            }
            if (registerRequest.getAcademicYearId() != null) {
                academicYearRepository.findById(registerRequest.getAcademicYearId()).ifPresent(studentProfile::setAcademicYear);
            }
            studentProfileRepository.save(studentProfile);
        } else if (roles.stream().anyMatch(r -> r.getName() == RoleType.ROLE_FACULTY)) {
            FacultyProfile facultyProfile = FacultyProfile.builder()
                    .user(savedUser)
                    .employeeId(registerRequest.getEmployeeId() != null ? registerRequest.getEmployeeId() : "FAC" + System.currentTimeMillis())
                    .designation("Assistant Professor")
                    .build();

            if (registerRequest.getDepartmentId() != null) {
                departmentRepository.findById(registerRequest.getDepartmentId()).ifPresent(facultyProfile::setDepartment);
            }
            facultyProfileRepository.save(facultyProfile);
        }

        return "User registered successfully!";
    }

    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles);

        if (roles.contains("ROLE_STUDENT")) {
            studentProfileRepository.findByUserEmail(email).ifPresent(student -> {
                builder.registerNumber(student.getRegisterNumber())
                        .currentSemester(student.getCurrentSemester())
                        .cgpa(student.getCgpa());
                if (student.getDepartment() != null) {
                    builder.departmentCode(student.getDepartment().getCode())
                           .departmentName(student.getDepartment().getName());
                }
                if (student.getCurrentClass() != null) {
                    builder.className(student.getCurrentClass().getName());
                }
                if (student.getAcademicYear() != null) {
                    builder.yearRange(student.getAcademicYear().getYearRange());
                }
            });
        } else if (roles.contains("ROLE_FACULTY")) {
            facultyProfileRepository.findByUserEmail(email).ifPresent(faculty -> {
                builder.employeeId(faculty.getEmployeeId());
                if (faculty.getDepartment() != null) {
                    builder.departmentCode(faculty.getDepartment().getCode())
                           .departmentName(faculty.getDepartment().getName());
                }
            });
        }

        return builder.build();
    }
}
