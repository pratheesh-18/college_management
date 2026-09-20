package com.edusphere.ai.service;

import com.edusphere.ai.dto.AiChatRequest;
import com.edusphere.ai.dto.AiChatResponse;
import com.edusphere.ai.dto.AiPlanResponse;
import com.edusphere.ai.dto.StudentAiContext;
import com.edusphere.ai.enums.AiIntent;
import com.edusphere.entity.AiChatMessage;
import com.edusphere.entity.User;
import com.edusphere.repository.AiChatMessageRepository;
import com.edusphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAiService {

    private final UserRepository userRepository;
    private final AiChatMessageRepository aiChatMessageRepository;
    private final StudentContextService contextService;
    private final GroqService groqService;
    private final TextToSqlService textToSqlService;
    private final AiConversationMemoryService memoryService;
    private final AiRateLimiterService rateLimiterService;

    /**
     * Reimplemented 2-Stage Hybrid LLM Chat Pipeline:
     * Stage 1: Groq LLM Plan & Intent Analysis (with Conversation Memory context)
     * Data Layer: Live DB Retrieval & Security Scoping (Authenticated JWT student)
     * Stage 2: Groq LLM Analyst & Dynamic Markdown Synthesis
     */
    public AiChatResponse processStudentChat(String email, AiChatRequest request) {
        if (!rateLimiterService.isAllowed(email)) {
            return AiChatResponse.builder()
                    .success(false)
                    .response("⚠️ Rate limit exceeded. Please wait a few seconds before asking another question.")
                    .conversationId(request.getConversationId())
                    .intent("RATE_LIMITED")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found for " + email));

        String queryMessage = request.getEffectiveMessage();
        if (queryMessage.isEmpty()) {
            queryMessage = "How is my academic performance?";
        }

        String conversationId = memoryService.getOrCreateConversationId(request.getConversationId());
        List<Map<String, String>> history = memoryService.getRecentMessages(conversationId);

        // STAGE 1: Groq LLM Planner & Intent Analysis (Context-aware follow-ups)
        AiPlanResponse plan = groqService.planQueryIntent(queryMessage, history);
        log.info("Stage 1 LLM Plan for [{}] -> intent: {}, requiresDb: {}, sources: {}, subjectFilter: {}, semFilter: {}, textToSql: {}",
                email, plan.getIntent(), plan.isRequiresDatabase(), plan.getRequiredSources(), plan.getSubjectFilter(), plan.getSemesterFilter(), plan.isUseTextToSql());

        // DATA LAYER: Retrieve Verified Live Student Data from Database
        Map<String, Object> verifiedDataContext = new LinkedHashMap<>();

        if (plan.isRequiresDatabase()) {
            if (plan.isUseTextToSql()) {
                try {
                    StudentAiContext context = contextService.buildContext(email, AiIntent.GENERAL_ACADEMIC);
                    String textToSqlResult = textToSqlService.processNaturalLanguageQuery(email, queryMessage, context);
                    verifiedDataContext.put("textToSqlExecutionReport", textToSqlResult);
                } catch (Exception e) {
                    log.warn("TextToSql execution fallback: {}", e.getMessage());
                    verifiedDataContext = contextService.buildTargetedContext(email, plan);
                }
            } else {
                verifiedDataContext = contextService.buildTargetedContext(email, plan);
            }
        }

        // STAGE 2: Groq LLM Analyst & Dynamic Markdown Response Generation
        String responseText = groqService.generateHybridResponse(queryMessage, verifiedDataContext, history);

        // Store prompt-response pair in Memory & Chat History Table
        memoryService.addMessagePair(conversationId, queryMessage, responseText);

        try {
            AiChatMessage entity = AiChatMessage.builder()
                    .user(user)
                    .prompt(queryMessage)
                    .response(responseText)
                    .timestamp(LocalDateTime.now())
                    .build();
            aiChatMessageRepository.save(entity);
        } catch (Exception e) {
            log.warn("Failed to persist AI message entity: {}", e.getMessage());
        }

        return AiChatResponse.builder()
                .success(true)
                .response(responseText)
                .conversationId(conversationId)
                .intent(plan.getIntent() != null ? plan.getIntent() : "HYBRID_LLM")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
