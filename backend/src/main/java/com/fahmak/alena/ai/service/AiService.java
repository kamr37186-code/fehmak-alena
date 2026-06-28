package com.fahmak.alena.ai.service;

import com.fahmak.alena.ai.dto.AiChatRequest;
import com.fahmak.alena.ai.dto.AiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for communicating with the OpenAI API.
 * Handles request construction, API invocation, and response parsing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private static final String DEFAULT_CONTEXT = "You are a helpful AI tutor.";
    private static final String FALLBACK_RESPONSE = "I'm sorry, I couldn't generate a response at this time.";

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public AiChatResponse getAiResponse(AiChatRequest request) {
        HttpEntity<Map<String, Object>> entity = buildRequestEntity(request);
        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = restTemplate.postForObject(apiUrl, entity, Map.class);
                String replyText = extractReplyText(responseBody);
                return new AiChatResponse(replyText);
            } catch (Exception e) {
                log.warn("Failed to communicate with OpenAI API (Attempt {} of {}): {}", attempt, maxRetries, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("All retries exhausted for OpenAI API", e);
                    return new AiChatResponse(FALLBACK_RESPONSE);
                }
                try {
                    Thread.sleep(1000 * attempt); // Exponential-ish backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new AiChatResponse(FALLBACK_RESPONSE);
                }
            }
        }
        return new AiChatResponse(FALLBACK_RESPONSE);
    }

    private HttpEntity<Map<String, Object>> buildRequestEntity(AiChatRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String context = request.getContext() != null ? request.getContext() : DEFAULT_CONTEXT;

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", context),
                Map.of("role", "user", "content", request.getMessage())
        );

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", messages,
                "temperature", 0.7
        );

        return new HttpEntity<>(requestBody, headers);
    }

    @SuppressWarnings("unchecked")
    private String extractReplyText(Map<String, Object> responseBody) {
        if (responseBody == null || !responseBody.containsKey("choices")) {
            return FALLBACK_RESPONSE;
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices.isEmpty()) return FALLBACK_RESPONSE;

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null || !message.containsKey("content")) return FALLBACK_RESPONSE;

        return (String) message.get("content");
    }
}
