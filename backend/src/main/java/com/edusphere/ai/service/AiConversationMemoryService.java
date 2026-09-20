package com.edusphere.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AiConversationMemoryService {

    private final Map<String, List<Map<String, String>>> memoryStore = new ConcurrentHashMap<>();
    private static final int MAX_MESSAGES_PER_CONVERSATION = 10;

    public String getOrCreateConversationId(String conversationId) {
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            return conversationId.trim();
        }
        return UUID.randomUUID().toString();
    }

    public List<Map<String, String>> getRecentMessages(String conversationId) {
        if (conversationId == null) return Collections.emptyList();
        return memoryStore.getOrDefault(conversationId, Collections.emptyList());
    }

    public void addMessagePair(String conversationId, String userMessage, String assistantResponse) {
        if (conversationId == null) return;

        List<Map<String, String>> history = memoryStore.computeIfAbsent(conversationId, k -> new ArrayList<>());
        synchronized (history) {
            history.add(Map.of("role", "user", "content", userMessage));
            history.add(Map.of("role", "assistant", "content", assistantResponse));

            // Evict oldest if exceeding limit
            while (history.size() > MAX_MESSAGES_PER_CONVERSATION) {
                history.remove(0);
            }
        }
    }

    public void clearConversation(String conversationId) {
        if (conversationId != null) {
            memoryStore.remove(conversationId);
        }
    }
}
