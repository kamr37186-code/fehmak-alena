package com.fahmak.alena.instructor.controller;

import com.fahmak.alena.instructor.dto.InstructorDashboardResponse;
import com.fahmak.alena.instructor.service.InstructorService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class InstructorControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private InstructorService instructorService;

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
        mockUser.setEmail("instructor@test.com");
        mockUser.setRole(Role.INSTRUCTOR);

        Mockito.when(userRepository.findByEmail(eq("instructor@test.com")))
               .thenReturn(Optional.of(mockUser));

        validToken = jwtService.generateToken(mockUser);
    }

    @Test
    void getDashboardData_ShouldReturnOk() throws Exception {
        InstructorDashboardResponse response = InstructorDashboardResponse.builder()
                .totalStudents(50)
                .build();

        Mockito.when(instructorService.getDashboardData(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/instructor/dashboard")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(50));
    }
}
