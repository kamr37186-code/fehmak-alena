package com.fahmak.alena.notification.controller;

import com.fahmak.alena.notification.dto.NotificationDTO;
import com.fahmak.alena.notification.service.NotificationService;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import com.fahmak.alena.user.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserRepository userRepository;

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
        mockUser.setEmail("user@test.com");
        mockUser.setRole(Role.STUDENT);

        Mockito.when(userRepository.findByEmail(eq("user@test.com")))
               .thenReturn(Optional.of(mockUser));

        validToken = jwtService.generateToken(mockUser);
    }

    @Test
    void getUserNotifications_ShouldReturnOk() throws Exception {
        NotificationDTO dto = NotificationDTO.builder().title("Alert").build();
        Mockito.when(notificationService.getUserNotifications(100L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notifications")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Alert"));
    }
}
