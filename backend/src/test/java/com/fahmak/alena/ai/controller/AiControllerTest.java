package com.fahmak.alena.ai.controller;

import com.fahmak.alena.ai.dto.AiChatRequest;
import com.fahmak.alena.ai.dto.AiChatResponse;
import com.fahmak.alena.ai.service.AiService;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.ai.repository.ChatRepository;
import com.fahmak.alena.user.repository.UserPreferencesRepository;
import com.fahmak.alena.user.repository.UserRepository;
import com.fahmak.alena.user.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AiControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AiService aiService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ChatRepository chatRepository;

    @MockitoBean
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private JwtService jwtService;

    private String validToken;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        User mockUser = new User();
        mockUser.setId(100L);
        mockUser.setEmail("ai@test.com");
        mockUser.setRole(Role.STUDENT);

        Mockito.when(userRepository.findByEmail(eq("ai@test.com")))
               .thenReturn(Optional.of(mockUser));

        validToken = jwtService.generateToken(mockUser);
    }

    @Test
    void chat_ShouldReturnOk() throws Exception {
        AiChatRequest request = new AiChatRequest("Hello", null);
        AiChatResponse response = new AiChatResponse("Hi there!");

        Mockito.when(aiService.getAiResponse(any(AiChatRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Hi there!"));
    }
}
