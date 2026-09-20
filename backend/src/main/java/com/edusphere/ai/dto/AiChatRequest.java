package com.edusphere.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    private String message;
    private String prompt;
    private String conversationId;

    public String getEffectiveMessage() {
        if (message != null && !message.trim().isEmpty()) {
            return message.trim();
        }
        if (prompt != null && !prompt.trim().isEmpty()) {
            return prompt.trim();
        }
        return "";
    }
}
