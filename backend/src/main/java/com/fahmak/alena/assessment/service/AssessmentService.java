package com.fahmak.alena.assessment.service;

import com.fahmak.alena.ai.dto.AiChatRequest;
import com.fahmak.alena.ai.dto.AiChatResponse;
import com.fahmak.alena.ai.service.AiService;
import com.fahmak.alena.assessment.dto.QuestionResponse;
import com.fahmak.alena.assessment.dto.QuizResponse;
import com.fahmak.alena.assessment.dto.SubmitAnswerResponse;
import com.fahmak.alena.assessment.dto.StartSessionRequest;
import com.fahmak.alena.assessment.dto.StartSessionResponse;
import com.fahmak.alena.assessment.entity.Question;
import com.fahmak.alena.assessment.entity.Quiz;
import com.fahmak.alena.assessment.entity.QuizAttempt;
import com.fahmak.alena.assessment.repository.QuestionRepository;
import com.fahmak.alena.assessment.repository.QuizAttemptRepository;
import com.fahmak.alena.assessment.repository.QuizRepository;
import com.fahmak.alena.assessment.repository.QuizSessionRepository;
import com.fahmak.alena.assessment.entity.QuizSession;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AiService aiService;
    private final UserService userService;
    private final QuizSessionRepository quizSessionRepository;

    @Transactional(readOnly = true)
    public List<QuizResponse> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::mapToQuizResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return mapToQuizResponse(quiz);
    }

    @Transactional
    public StartSessionResponse startSession(StartSessionRequest request, String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(request.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        QuizSession session = new QuizSession();
        session.setUser(user);
        session.setQuiz(quiz);
        session.setStartedAt(LocalDateTime.now());
        
        quizSessionRepository.save(session);
        return new StartSessionResponse(session.getId());
    }

    @Transactional
    public SubmitAnswerResponse submitAnswer(Long questionId, String selectedAnswer, String userEmail) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        boolean isCorrect = selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer());

        if (userEmail != null) {
            User user = userService.findByEmail(userEmail).orElse(null);
            if (user != null) {
                QuizAttempt attempt = new QuizAttempt();
                attempt.setUser(user);
                attempt.setQuestion(question);
                attempt.setStudentAnswer(selectedAnswer);
                attempt.setCorrect(isCorrect);
                attempt.setAttemptDate(LocalDateTime.now());
                quizAttemptRepository.save(attempt);
            }
        }

        // Generate AI Feedback based on the answer
        String context = "You are an AI Tutor evaluating a student's answer to a question. " +
                         "Question: " + question.getQuestionText() + " " +
                         "Correct Answer: " + question.getCorrectAnswer() + " " +
                         "Student Answer: " + selectedAnswer + " " +
                         "Is Correct: " + isCorrect;
                         
        String prompt = "Provide a brief, encouraging real-time analysis and logic breakdown of the student's answer. " +
                        "If it is correct, explain why it's right. If incorrect, explain the misconception without giving away the exact answer immediately, and suggest a brief learning resource topic.";

        AiChatResponse aiResponse = aiService.getAiResponse(new AiChatRequest(prompt, context));

        return SubmitAnswerResponse.builder()
                .isCorrect(isCorrect)
                .aiFeedback(aiResponse.getReply())
                .explanation(question.getExplanation())
                .build();
    }

    private QuizResponse mapToQuizResponse(Quiz quiz) {
        List<QuestionResponse> questionResponses = null;
        if (quiz.getQuestions() != null) {
            questionResponses = quiz.getQuestions().stream()
                    .map(q -> QuestionResponse.builder()
                            .id(q.getId())
                            .questionText(q.getQuestionText())
                            .options(q.getOptions())
                            .type(q.getQuestionType())
                            .build())
                    .collect(Collectors.toList());
        }

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .difficultyLevel(quiz.getDifficultyLevel())
                .questions(questionResponses)
                .build();
    }
}
