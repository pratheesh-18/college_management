package com.edusphere.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {
    private boolean success;
    private String response;
    private String conversationId;
    private String intent;
    private LocalDateTime timestamp;
}
