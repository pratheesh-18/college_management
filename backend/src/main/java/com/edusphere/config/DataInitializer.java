package com.edusphere.config;

import com.edusphere.entity.*;
import com.edusphere.enums.MarkType;
import com.edusphere.enums.RiskLevel;
import com.edusphere.enums.RoleType;
import com.edusphere.enums.VerificationStatus;
import com.edusphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final SubjectAllocationRepository subjectAllocationRepository;
    private final InternalMarkRepository internalMarkRepository;
    private final SemesterMarkRepository semesterMarkRepository;
    private final CertificateRepository certificateRepository;
    private final AcademicRiskAlertRepository riskAlertRepository;
    private final NotificationRepository notificationRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Roles
        Role adminRole = roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build());
        Role facultyRole = roleRepository.save(Role.builder().name(RoleType.ROLE_FACULTY).build());
        Role studentRole = roleRepository.save(Role.builder().name(RoleType.ROLE_STUDENT).build());

        // 2. Departments
        Department it = departmentRepository.save(Department.builder().code("IT").name("Information Technology").description("Department of Information Technology").build());
        Department cse = departmentRepository.save(Department.builder().code("CSE").name("Computer Science & Engineering").description("Department of Computer Science").build());
        Department ece = departmentRepository.save(Department.builder().code("ECE").name("Electronics & Communication Engineering").description("Department of Electronics").build());
        Department mech = departmentRepository.save(Department.builder().code("MECH").name("Mechanical Engineering").description("Department of Mechanical").build());

        // 3. Academic Years
        AcademicYear year2327 = academicYearRepository.save(AcademicYear.builder().yearRange("2023-2027").active(true).build());
        AcademicYear year2428 = academicYearRepository.save(AcademicYear.builder().yearRange("2024-2028").active(true).build());
        AcademicYear year2627 = academicYearRepository.save(AcademicYear.builder().yearRange("2026-2027").active(true).build());

        // 4. IT Classes
        ClassEntity it3a = classRepository.save(ClassEntity.builder().name("III IT A").semester(5).section("A").department(it).academicYear(year2327).build());
        ClassEntity it2a = classRepository.save(ClassEntity.builder().name("II IT A").semester(3).section("A").department(it).academicYear(year2428).build());
        ClassEntity cse3a = classRepository.save(ClassEntity.builder().name("CSE-3A").semester(5).section("A").department(cse).academicYear(year2327).build());
        ClassEntity ece2b = classRepository.save(ClassEntity.builder().name("ECE-2B").semester(3).section("B").department(ece).academicYear(year2428).build());

        // 5. IT Subjects
        Subject javaProg = subjectRepository.save(Subject.builder().code("IT301").name("Java Programming").credits(4).semester(3).department(it).build());
        Subject dbmsIt = subjectRepository.save(Subject.builder().code("IT302").name("Database Management Systems").credits(4).semester(3).department(it).build());
        Subject netIt = subjectRepository.save(Subject.builder().code("IT303").name("Computer Networks").credits(3).semester(5).department(it).build());
        Subject webTech = subjectRepository.save(Subject.builder().code("IT304").name("Web Technologies & Cloud").credits(3).semester(5).department(it).build());
        
        Subject dsa = subjectRepository.save(Subject.builder().code("CS301").name("Data Structures and Algorithms").credits(4).semester(5).department(cse).build());

        // 6. Admin User (IT Dept Admin)
        User admin = User.builder()
                .email("admin@edusphere.edu")
                .password(passwordEncoder.encode("admin123"))
                .fullName("IT Department Administrator")
                .roles(Set.of(adminRole))
                .build();
        userRepository.save(admin);

        // 7. IT Faculty Users & Profiles
        User facultyUser1 = User.builder()
                .email("faculty@edusphere.edu")
                .password(passwordEncoder.encode("faculty123"))
                .fullName("Dr. Ramesh Kumar")
                .roles(Set.of(facultyRole))
                .build();
        User savedFacultyUser1 = userRepository.save(facultyUser1);

        FacultyProfile faculty1 = facultyProfileRepository.save(FacultyProfile.builder()
                .user(savedFacultyUser1)
                .employeeId("ITFAC101")
                .designation("Senior Professor")
                .department(it)
                .build());

        User facultyUser2 = User.builder()
                .email("priya@edusphere.edu")
                .password(passwordEncoder.encode("priya123"))
                .fullName("Prof. Priya Sharma")
                .roles(Set.of(facultyRole))
                .build();
        User savedFacultyUser2 = userRepository.save(facultyUser2);

        FacultyProfile faculty2 = facultyProfileRepository.save(FacultyProfile.builder()
                .user(savedFacultyUser2)
                .employeeId("ITFAC102")
                .designation("Assistant Professor")
                .department(it)
                .build());

        // Set Tutors and Advisors for IT Classes
        it2a.setTutor(faculty1);
        it2a.setAdvisor(faculty2);
        classRepository.save(it2a);

        it3a.setTutor(faculty2);
        it3a.setAdvisor(faculty1);
        classRepository.save(it3a);

        // Subject Allocations
        subjectAllocationRepository.save(SubjectAllocation.builder().faculty(faculty1).subject(javaProg).classEntity(it2a).build());
        subjectAllocationRepository.save(SubjectAllocation.builder().faculty(faculty2).subject(dbmsIt).classEntity(it2a).build());
        subjectAllocationRepository.save(SubjectAllocation.builder().faculty(faculty1).subject(netIt).classEntity(it3a).build());

        // 8. IT Student Users & Profiles
        User studentUser1 = User.builder()
                .email("student@edusphere.edu")
                .password(passwordEncoder.encode("student123"))
                .fullName("Rahul Verma")
                .roles(Set.of(studentRole))
                .build();
        User savedStudent1 = userRepository.save(studentUser1);

        StudentProfile student1 = studentProfileRepository.save(StudentProfile.builder()
                .user(savedStudent1)
                .registerNumber("IT2024001")
                .currentSemester(3)
                .cgpa(8.65)
                .phone("+91 9876543210")
                .gender("Male")
                .dateOfBirth("2004-05-15")
                .department(it)
                .currentClass(it2a)
                .academicYear(year2428)
                .leetcodeUsername("rahulverma_dev")
                .githubUsername("rahulverma-code")
                .build());

        User studentUser2 = User.builder()
                .email("alex@edusphere.edu")
                .password(passwordEncoder.encode("alex123"))
                .fullName("Ananya Sen")
                .roles(Set.of(studentRole))
                .build();
        User savedStudent2 = userRepository.save(studentUser2);

        StudentProfile student2 = studentProfileRepository.save(StudentProfile.builder()
                .user(savedStudent2)
                .registerNumber("IT2024002")
                .currentSemester(3)
                .cgpa(5.80)
                .phone("+91 9876543211")
                .gender("Female")
                .dateOfBirth("2004-08-22")
                .department(it)
                .currentClass(it2a)
                .academicYear(year2428)
                .leetcodeUsername("ananyasen")
                .githubUsername("ananyasen-dev")
                .build());

        // 9. Internal Marks
        internalMarkRepository.save(InternalMark.builder().student(student1).subject(javaProg).markType(MarkType.INTERNAL_1).marksObtained(45.0).maxMarks(50.0).semester(3).build());
        internalMarkRepository.save(InternalMark.builder().student(student1).subject(javaProg).markType(MarkType.INTERNAL_2).marksObtained(48.0).maxMarks(50.0).semester(3).build());
        internalMarkRepository.save(InternalMark.builder().student(student1).subject(dbmsIt).markType(MarkType.INTERNAL_1).marksObtained(42.0).maxMarks(50.0).semester(3).build());

        internalMarkRepository.save(InternalMark.builder().student(student2).subject(javaProg).markType(MarkType.INTERNAL_1).marksObtained(18.0).maxMarks(50.0).semester(3).build()); // Low mark trigger!

        // 10. Semester Marks
        // Sem 1
        semesterMarkRepository.save(SemesterMark.builder().student(student1).subject(javaProg).semester(1).gradePoints(9.0).letterGrade("A+").marksObtained(88.0).maxMarks(100.0).build());
        semesterMarkRepository.save(SemesterMark.builder().student(student1).subject(dbmsIt).semester(1).gradePoints(8.0).letterGrade("A").marksObtained(81.0).maxMarks(100.0).build());
        // Sem 2
        semesterMarkRepository.save(SemesterMark.builder().student(student1).subject(netIt).semester(2).gradePoints(8.5).letterGrade("A+").marksObtained(85.0).maxMarks(100.0).build());
        semesterMarkRepository.save(SemesterMark.builder().student(student1).subject(webTech).semester(2).gradePoints(9.5).letterGrade("O").marksObtained(94.0).maxMarks(100.0).build());

        // 11. Academic History Records
        academicRecordRepository.save(AcademicRecord.builder().student(student1).academicYear(year2327).semester(1).gpa(8.50).totalCredits(14).passedSubjects(4).failedSubjects(0).recordedAt(LocalDateTime.now().minusMonths(12)).build());
        academicRecordRepository.save(AcademicRecord.builder().student(student1).academicYear(year2428).semester(2).gpa(9.00).totalCredits(14).passedSubjects(4).failedSubjects(0).recordedAt(LocalDateTime.now().minusMonths(6)).build());

        // 12. Certificates
        certificateRepository.save(Certificate.builder()
                .student(student1)
        .title("NPTEL Cloud Computing Certification")
        .category("Online Course")
        .issueOrganization("IIT Kharagpur / NPTEL")
        .fileUrl("https://example.com/certs/nptel_cloud.pdf")
        .status(VerificationStatus.APPROVED)
        .reviewerComments("Verified successfully. Elite tag awarded.")
        .uploadedAt(LocalDateTime.now().minusDays(5))
        .build());

        certificateRepository.save(Certificate.builder()
                .student(student1)
                .title("National Level Hackathon Winner")
                .category("Hackathon")
                .issueOrganization("IEEE Student Chapter")
                .fileUrl("https://example.com/certs/hackathon_win.pdf")
                .status(VerificationStatus.PENDING)
                .reviewerComments(null)
                .uploadedAt(LocalDateTime.now().minusDays(1))
                .build());

        // 13. Notifications
        notificationRepository.save(Notification.builder()
                .student(student1)
                .title("🎉 Performance Improvement Alert")
                .message("Your Java Programming Internal 2 score improved from 45/50 to 48/50 (+6% mark boost). Excellent work!")
                .type("MARK_UPDATE")
                .isRead(false)
                .createdAt(LocalDateTime.now().minusHours(3))
                .build());

        notificationRepository.save(Notification.builder()
                .student(student1)
                .title("✅ Certificate Verified")
                .message("Your 'NPTEL Cloud Computing Certification' was verified and approved by Dr. Ramesh Kumar.")
                .type("CERTIFICATE_STATUS")
                .isRead(true)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());

        // 14. Academic Risk Alert
        riskAlertRepository.save(AcademicRiskAlert.builder()
                .student(student2)
                .riskLevel(RiskLevel.HIGH)
                .reason("Overall CGPA is 5.80 (below 6.0 threshold) and scored 36% in DSA Internal 1.")
                .recommendedAction("Assign peer tutor and schedule mandatory faculty office hours.")
                .createdAt(LocalDateTime.now())
                .resolved(false)
                .build());
    }
}
