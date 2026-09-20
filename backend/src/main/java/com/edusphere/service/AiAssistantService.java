package com.edusphere.service;

import com.edusphere.ai.dto.AiChatRequest;
import com.edusphere.ai.dto.AiChatResponse;
import com.edusphere.ai.service.StudentAiService;
import com.edusphere.dto.AiChatDTO;
import com.edusphere.entity.AiChatMessage;
import com.edusphere.repository.AiChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final AiChatMessageRepository aiChatMessageRepository;
    private final StudentAiService studentAiService;

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

    /**
     * Delegates to StudentAiService 2-Stage Hybrid LLM Pipeline (zero static canned string maps).
     */
    public AiChatDTO askAiAssistant(String email, String prompt) {
        AiChatRequest request = new AiChatRequest();
        request.setMessage(prompt);

        AiChatResponse response = studentAiService.processStudentChat(email, request);

        return AiChatDTO.builder()
                .prompt(prompt)
                .response(response.getResponse())
                .timestamp(response.getTimestamp() != null ? response.getTimestamp() : LocalDateTime.now())
                .build();
    }
}
