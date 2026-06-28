package com.fahmak.alena.assessment.controller;

import com.fahmak.alena.assessment.dto.QuizResponse;
import com.fahmak.alena.assessment.service.AssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AssessmentControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private AssessmentService assessmentService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser
    void getAllQuizzes_ShouldReturnOk() throws Exception {
        QuizResponse response = QuizResponse.builder().id(1L).title("Quiz 1").build();

        Mockito.when(assessmentService.getAllQuizzes()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/assessments/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Quiz 1"));
    }
}
