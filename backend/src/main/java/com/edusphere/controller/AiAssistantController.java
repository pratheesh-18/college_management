package com.edusphere.controller;

import com.edusphere.dto.AiChatDTO;
import com.edusphere.service.AiAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    @GetMapping("/history")
    public ResponseEntity<List<AiChatDTO>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(aiAssistantService.getChatHistory(authentication.getName()));
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatDTO> askAi(@RequestBody Map<String, String> payload, Authentication authentication) {
        String prompt = payload.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = "Hello AI Assistant";
        }
        return ResponseEntity.ok(aiAssistantService.askAiAssistant(authentication.getName(), prompt));
    }
}
