package com.crimsonlogic.open_petal_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.crimsonlogic.open_petal_backend.dto.aichat.*;
import com.crimsonlogic.open_petal_backend.entity.AiChat;
import com.crimsonlogic.open_petal_backend.entity.AiChatMessage;
import com.crimsonlogic.open_petal_backend.repository.AiChatMessageRepository;
import com.crimsonlogic.open_petal_backend.repository.AiChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Service interface for ai chat operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private final AiChatRepository chatRepository;
    private final AiChatMessageRepository chatMessageRepository;
    private final JdbcTemplate jdbcTemplate;
    private final GeminiAiClient geminiClient;
    /**
     * Object Mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlatformTransactionManager transactionManager;

    @Value("classpath:prompts/system-prompt.txt")
    private Resource systemPromptResource;

    private static final Pattern SAFE_SELECT_PATTERN = 
        /**
         * Compile.
         */
        Pattern.compile("^\\s*(SELECT|WITH)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FORBIDDEN_SQL_PATTERN = 
        /**
         * Compile.
         */
        Pattern.compile("\\b(INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|GRANT|REVOKE|EXEC|CREATE)\\b", Pattern.CASE_INSENSITIVE);

    public ChatResponse processChat(String optionalChatId, ChatRequest req, UserContext user) {
        
        // 1. Session Setup (Pre-assign ID so it is never null)
        AiChat chat;
        if (req.isNewChat() || optionalChatId == null) {
            /**
             * Ai Chat.
             */
            chat = new AiChat();
            /**
             * Set Id.
             */
            chat.setId(UUID.randomUUID().toString());
            /**
             * Set User Id.
             */
            chat.setUserId(user.getId());
            /**
             * Set Title.
             */
            chat.setTitle(req.getPrompt().substring(0, Math.min(req.getPrompt().length(), 40)) + "...");
            /**
             * Set Summary.
             */
            chat.setSummary("New conversation started");
            /**
             * Set Last Message At.
             */
            chat.setLastMessageAt(LocalDateTime.now());
        } else {
            chat = chatRepository.findByIdAndUserId(optionalChatId, user.getId())
                    /**
                     * Or Else Throw.
                     */
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat session not found"));
        }

        // 2. Build Payload (Outside of DB Transaction)
        String payload = String.format("""
            [USER_CONTEXT] {"user_id":"%s", "user_role":"%s", "user_name":"%s"}
            [PREVIOUS_SUMMARY] %s
            [USER_QUERY] %s
            [new_chat]: %s
            """,
            user.getId(), user.getRole(), user.getName(),
            (req.getNextGenSummary() != null ? req.getNextGenSummary() : "None"),
            req.getPrompt(),
            (req.isNewChat() ? "new chat so please provide title" : "title is not required old chat")
        );

        // 3. Remote LLM Call
        /**
         * Generate Content.
         */
        String rawJsonResponse = geminiClient.generateContent(loadSystemPrompt(), payload);

        // 4. Parse Sanitized JSON
        ChatResponse aiParsed;
        try {
            /**
             * Extract Pure Json.
             */
            String cleanJson = extractPureJson(rawJsonResponse);
            /**
             * Read Value.
             */
            aiParsed = objectMapper.readValue(cleanJson, ChatResponse.class);
        } catch (Exception e) {
            /**
             * Error.
             */
            log.error("Failed to parse Gemini response: {}", rawJsonResponse, e);
            /**
             * Response Status Exception.
             */
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI returned invalid response format");
        }

        // 5. Execute Read-Only SQL
        /**
         * Execute Queries.
         */
        List<Dataset> datasets = executeQueries(aiParsed);

        // 6. Handle Fallback & Output Assembly
        /**
         * Stream.
         */
        int totalRows = datasets.stream().mapToInt(Dataset::getRows).sum();
        if (totalRows == 0 && aiParsed.getEmptyResultFallback() != null) {
            if (aiParsed.getAiText() == null) {
                /**
                 * Set Ai Text.
                 */
                aiParsed.setAiText(new AiText());
            }
            /**
             * Get Ai Text.
             */
            aiParsed.getAiText().setOutroMessage(aiParsed.getEmptyResultFallback());
        }

        /**
         * Set Chat Id.
         */
        aiParsed.setChatId(chat.getId());
        /**
         * Set From Database.
         */
        aiParsed.setFromDatabase(datasets);
        /**
         * Set Canvas.
         */
        aiParsed.setCanvas(aiParsed.getAiText() != null && aiParsed.getAiText().getToCanvas() != null);

        // 7. Persist via TransactionTemplate (Ensures atomic commit without self-invocation issues)
        /**
         * Transaction Template.
         */
        TransactionTemplate writeTemplate = new TransactionTemplate(transactionManager);
        writeTemplate.executeWithoutResult(status -> {
            /**
             * Set Last Message At.
             */
            chat.setLastMessageAt(LocalDateTime.now());
            /**
             * Set Summary.
             */
            chat.setSummary(aiParsed.getNextGenSummary() != null ? aiParsed.getNextGenSummary() : chat.getSummary());
            if (req.isNewChat() && aiParsed.getTitle() != null) {
                /**
                 * Set Title.
                 */
                chat.setTitle(aiParsed.getTitle());
            }
            /**
             * Save.
             */
            AiChat savedChat = chatRepository.save(chat);

            Map<String, Object> historyContent = new HashMap<>();
            /**
             * Put.
             */
            historyContent.put("userQuery", req.getPrompt());
            /**
             * Put.
             */
            historyContent.put("response", aiParsed);

            /**
             * Ai Chat Message.
             */
            AiChatMessage msg = new AiChatMessage();
            /**
             * Set Chat.
             */
            msg.setChat(savedChat);
            /**
             * Set Role.
             */
            msg.setRole(user.getRole());
            /**
             * Set Content.
             */
            msg.setContent(historyContent);
            /**
             * Save.
             */
            chatMessageRepository.save(msg);
        });

        return aiParsed;
    }

    private List<Dataset> executeQueries(ChatResponse aiParsed) {
        List<SqlQuery> queriesToRun = new ArrayList<>();
        /**
         * If.
         */
        if (aiParsed.getQueryList() != null) queriesToRun.addAll(aiParsed.getQueryList());
        /**
         * If.
         */
        if (aiParsed.getSqlQuery() != null) queriesToRun.add(aiParsed.getSqlQuery());

        List<Dataset> datasets = new ArrayList<>();
        /**
         * Transaction Template.
         */
        TransactionTemplate readOnlyTemplate = new TransactionTemplate(transactionManager);
        /**
         * Set Read Only.
         */
        readOnlyTemplate.setReadOnly(true);

        for (SqlQuery q : queriesToRun) {
            /**
             * Dataset.
             */
            Dataset ds = new Dataset();
            /**
             * Set Label.
             */
            ds.setLabel(q.getLabel() != null ? q.getLabel() : "Query Result");

            if (q.getSql() == null || !isSafeSql(q.getSql())) {
                /**
                 * Set Label.
                 */
                ds.setLabel("Blocked Unsafe SQL: " + ds.getLabel());
                /**
                 * Set Data.
                 */
                ds.setData(Collections.emptyList());
                /**
                 * Add.
                 */
                datasets.add(ds);
                continue;
            }

            try {
                List<Map<String, Object>> rows = readOnlyTemplate.execute(status -> 
                    jdbcTemplate.queryForList(q.getSql())
                );
                /**
                 * Empty List.
                 */
                List<Map<String, Object>> safeRows = (rows != null) ? rows : Collections.emptyList();
                /**
                 * Set Data.
                 */
                ds.setData(safeRows);
                /**
                 * Set Rows.
                 */
                ds.setRows(safeRows.size());
                /**
                 * Set Columns.
                 */
                ds.setColumns(safeRows.isEmpty() ? 0 : safeRows.get(0).size());
            } catch (Exception e) {
                /**
                 * Warn.
                 */
                log.warn("SQL execution failed for query: {}", q.getSql(), e);
                /**
                 * Set Label.
                 */
                ds.setLabel("Error: " + ds.getLabel());
                /**
                 * Set Data.
                 */
                ds.setData(Collections.emptyList());
            }
            /**
             * Add.
             */
            datasets.add(ds);
        }
        return datasets;
    }

    private boolean isSafeSql(String sql) {
        /**
         * Matcher.
         */
        return SAFE_SELECT_PATTERN.matcher(sql).find() && !FORBIDDEN_SQL_PATTERN.matcher(sql).find();
    }

    private String extractPureJson(String raw) {
        /**
         * If.
         */
        if (raw == null) return "{}";
        /**
         * Trim.
         */
        String s = raw.trim();
        /**
         * If.
         */
        if (s.startsWith("```json")) s = s.substring(7);
        /**
         * If.
         */
        else if (s.startsWith("```")) s = s.substring(3);
        /**
         * If.
         */
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        /**
         * Trim.
         */
        return s.trim();
    }

    private String loadSystemPrompt() {
        try {
            /**
             * Get Content As String.
             */
            return systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            /**
             * Error.
             */
            log.error("Failed to load system prompt", e);
            return "You are an AI assistant.";
        }
    }
}
