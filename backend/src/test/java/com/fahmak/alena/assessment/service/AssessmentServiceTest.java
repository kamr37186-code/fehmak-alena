package com.fahmak.alena.assessment.service;

import com.fahmak.alena.ai.dto.AiChatResponse;
import com.fahmak.alena.ai.service.AiService;
import com.fahmak.alena.assessment.dto.QuizResponse;
import com.fahmak.alena.assessment.dto.StartSessionRequest;
import com.fahmak.alena.assessment.dto.StartSessionResponse;
import com.fahmak.alena.assessment.dto.SubmitAnswerResponse;
import com.fahmak.alena.assessment.entity.Question;
import com.fahmak.alena.assessment.entity.Quiz;
import com.fahmak.alena.assessment.repository.QuestionRepository;
import com.fahmak.alena.assessment.repository.QuizAttemptRepository;
import com.fahmak.alena.assessment.repository.QuizRepository;
import com.fahmak.alena.assessment.repository.QuizSessionRepository;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssessmentServiceTest {

    @Mock private QuizRepository quizRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private QuizAttemptRepository quizAttemptRepository;
    @Mock private AiService aiService;
    @Mock private UserService userService;
    @Mock private QuizSessionRepository quizSessionRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllQuizzes_ShouldReturnList() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        when(quizRepository.findAll()).thenReturn(List.of(quiz));

        List<QuizResponse> result = assessmentService.getAllQuizzes();

        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTitle());
    }

    @Test
    void startSession_ShouldCreateSession() {
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));
        Quiz quiz = new Quiz();
        quiz.setId(10L);
        when(quizRepository.findById(10L)).thenReturn(Optional.of(quiz));

        StartSessionRequest request = new StartSessionRequest();
        request.setQuizId(10L);

        StartSessionResponse response = assessmentService.startSession(request, "test@test.com");

        assertNotNull(response);
        verify(quizSessionRepository).save(any());
    }

    @Test
    void submitAnswer_CorrectAnswer_ShouldReturnTrueAndAiFeedback() {
        Question q = new Question();
        q.setId(1L);
        q.setCorrectAnswer("A");
        q.setQuestionText("What is 2+2?");
        q.setExplanation("Basic math");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(q));
        
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));
        when(aiService.getAiResponse(any())).thenReturn(new AiChatResponse("Good job!"));

        SubmitAnswerResponse response = assessmentService.submitAnswer(1L, "A", "test@test.com");

        assertTrue(response.isCorrect());
        assertEquals("Good job!", response.getAiFeedback());
        assertEquals("Basic math", response.getExplanation());
        verify(quizAttemptRepository).save(any());
    }
}
