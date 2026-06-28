package com.fahmak.alena.gamification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fahmak.alena.gamification.service.GamificationService;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import com.fahmak.alena.user.security.JwtService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class GamificationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private GamificationService gamificationService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private User mockUser;
    private String validToken;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockUser = new User();
        mockUser.setId(100L);
        mockUser.setEmail("student@test.com");
        mockUser.setRole(Role.STUDENT);

        Mockito.when(userRepository.findByEmail(eq("student@test.com")))
               .thenReturn(Optional.of(mockUser));

        validToken = jwtService.generateToken(mockUser);
    }

    @Test
    public void testAwardXpSuccess() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("activityType", "QUIZ_COMPLETION");
        payload.put("xp", 50);

        // Act & Assert (Sending to our OWN user ID, 100)
        mockMvc.perform(post("/api/gamification/users/100/award")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("XP awarded successfully"));
        
        Mockito.verify(gamificationService, Mockito.times(1)).awardXpSecure(eq("student@test.com"), eq(100L), Mockito.any());
    }

    @Test
    public void testAwardXpIdorProtection_Forbidden() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("activityType", "QUIZ_COMPLETION");
        payload.put("xp", 5000);

        Mockito.doThrow(new SecurityException("You cannot award XP to another user."))
                .when(gamificationService).awardXpSecure(eq("student@test.com"), eq(999L), Mockito.any());

        // Act & Assert (Sending to ANOTHER user ID, 999)
        mockMvc.perform(post("/api/gamification/users/999/award")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You cannot award XP to another user."));
        
        // Ensure service was called and threw the exception
        Mockito.verify(gamificationService, Mockito.times(1)).awardXpSecure(eq("student@test.com"), eq(999L), Mockito.any());
    }

    @Test
    public void testAwardXpUnauthorized_NoToken() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("activityType", "QUIZ_COMPLETION");
        payload.put("xp", 50);

        // Act & Assert (NO TOKEN)
        mockMvc.perform(post("/api/gamification/users/100/award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden()); // Spring Security denies missing token
    }
}
