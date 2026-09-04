package com.edusphere.service;

import com.edusphere.dto.AiChatDTO;
import com.edusphere.entity.AiChatMessage;
import com.edusphere.entity.StudentProfile;
import com.edusphere.entity.User;
import com.edusphere.repository.AiChatMessageRepository;
import com.edusphere.repository.StudentProfileRepository;
import com.edusphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final AiChatMessageRepository aiChatMessageRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    public List<AiChatDTO> getChatHistory(String email) {
        return aiChatMessageRepository.findByUserEmailOrderByTimestampAsc(email).stream()
                .map(msg -> AiChatDTO.builder()
                        .id(msg.getId())
                        .prompt(msg.getPrompt())
                        .response(msg.getResponse())
                        .timestamp(msg.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    public AiChatDTO askAiAssistant(String email, String prompt) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<StudentProfile> studentOpt = studentProfileRepository.findByUserEmail(email);

        String generatedResponse = generateContextualAiResponse(user, studentOpt, prompt);

        AiChatMessage message = AiChatMessage.builder()
                .user(user)
                .prompt(prompt)
                .response(generatedResponse)
                .timestamp(LocalDateTime.now())
                .build();

        AiChatMessage saved = aiChatMessageRepository.save(message);

        return AiChatDTO.builder()
                .id(saved.getId())
                .prompt(saved.getPrompt())
                .response(saved.getResponse())
                .timestamp(saved.getTimestamp())
                .build();
    }

    private String generateContextualAiResponse(User user, Optional<StudentProfile> studentOpt, String prompt) {
        String lowerPrompt = prompt.toLowerCase();

        if (studentOpt.isPresent()) {
            StudentProfile student = studentOpt.get();
            double cgpa = student.getCgpa() != null ? student.getCgpa() : 0.0;
            int semester = student.getCurrentSemester() != null ? student.getCurrentSemester() : 1;
            String dept = student.getDepartment() != null ? student.getDepartment().getName() : "your department";

            if (lowerPrompt.contains("gpa") || lowerPrompt.contains("cgpa") || lowerPrompt.contains("score") || lowerPrompt.contains("grade")) {
                return String.format(
                        "Hello %s! Your current cumulative GPA (CGPA) is **%.2f** in Semester %d (%s). " +
                        "To boost your CGPA above %.1f, focus on high-credit core subjects and target at least an 'A+' grade in internal assessments. Would you like a subject-by-subject study plan?",
                        user.getFullName(), cgpa, semester, dept, Math.min(10.0, cgpa + 0.5)
                );
            } else if (lowerPrompt.contains("risk") || lowerPrompt.contains("warning") || lowerPrompt.contains("help")) {
                if (cgpa < 6.5) {
                    return String.format(
                            "⚠️ **Academic Advisory Alert for %s**: Your CGPA (%.2f) is currently below the recommended 7.0 target. " +
                            "Here is your personalized recovery plan:\n" +
                            "1. Allocate 2 hours daily for active recall in weak subjects.\n" +
                            "2. Request 1-on-1 guidance from your faculty advisor.\n" +
                            "3. Complete pending assignments before internal test 2 to maximize internal marks.",
                            user.getFullName(), cgpa
                    );
                } else {
                    return String.format(
                            "🌟 **Academic Status Update**: Great job %s! Your CGPA (%.2f) is healthy. " +
                            "Keep maintaining your attendance and internal marks above 85%% to secure an Honours distinction.",
                            user.getFullName(), cgpa
                    );
                }
            } else if (lowerPrompt.contains("certificate") || lowerPrompt.contains("nptel") || lowerPrompt.contains("project")) {
                return "📜 **Academic Enrichment Suggestion**: Uploading verified certifications (NPTEL, Coursera, Hackathons) enhances your academic credits and industry readiness. Navigate to the **Certificates** portal in your dashboard to submit new credentials!";
            } else if (lowerPrompt.contains("schedule") || lowerPrompt.contains("timetable") || lowerPrompt.contains("exam")) {
                return String.format(
                        "📅 **Academic Calendar**: You are in Semester %d. Mid-semester assessments are scheduled according to your class syllabus (%s). Check your Faculty Announcements for exact date sheets.",
                        semester, student.getCurrentClass() != null ? student.getCurrentClass().getName() : "Section A"
                );
            }
        }

        return String.format(
                "🤖 **EduSphere AI Academic Advisor**: Welcome %s! I am your AI academic intelligence assistant. " +
                "I can analyze your internal and semester marks, compare performance trends, calculate target CGPAs, recommend study routines, and monitor academic risk. " +
                "How can I assist your academic journey today?",
                user.getFullName()
        );
    }
}
