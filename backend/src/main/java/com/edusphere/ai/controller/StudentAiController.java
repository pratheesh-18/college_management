package com.edusphere.ai.controller;

import com.edusphere.ai.dto.AiChatRequest;
import com.edusphere.ai.dto.AiChatResponse;
import com.edusphere.ai.service.StudentAiService;
import com.edusphere.dto.AiChatDTO;
import com.edusphere.service.AiAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/api/v1/student/ai", "/api/student/ai"})
@RequiredArgsConstructor
public class StudentAiController {

    private final StudentAiService studentAiService;
    private final AiAssistantService aiAssistantService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chatWithAi(
            @RequestBody AiChatRequest request,
            Authentication authentication) {
        String studentEmail = authentication.getName();
        return ResponseEntity.ok(studentAiService.processStudentChat(studentEmail, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AiChatDTO>> getChatHistory(Authentication authentication) {
        return ResponseEntity.ok(aiAssistantService.getChatHistory(authentication.getName()));
    }
}
