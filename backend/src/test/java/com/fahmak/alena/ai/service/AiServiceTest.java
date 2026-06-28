package com.fahmak.alena.ai.service;

import com.fahmak.alena.ai.dto.AiChatRequest;
import com.fahmak.alena.ai.dto.AiChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AiService aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(aiService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(aiService, "apiUrl", "https://api.openai.com/v1/chat/completions");
    }

    @Test
    void getAiResponse_Success() {
        Map<String, Object> message = Map.of("content", "Hello from AI!");
        Map<String, Object> choice = Map.of("message", message);
        Map<String, Object> mockResponse = Map.of("choices", List.of(choice));

        when(restTemplate.postForObject(eq("https://api.openai.com/v1/chat/completions"), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        AiChatRequest request = new AiChatRequest("Hello", "Context");
        AiChatResponse response = aiService.getAiResponse(request);

        assertNotNull(response);
        assertEquals("Hello from AI!", response.getReply());
    }

    @Test
    void getAiResponse_ApiFailure_ReturnsFallback() {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("API Error"));

        AiChatRequest request = new AiChatRequest("Hello", null);
        AiChatResponse response = aiService.getAiResponse(request);

        assertNotNull(response);
        assertEquals("I'm sorry, I couldn't generate a response at this time.", response.getReply());
    }
}
