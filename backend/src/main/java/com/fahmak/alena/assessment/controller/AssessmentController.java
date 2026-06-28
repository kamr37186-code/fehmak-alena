package com.fahmak.alena.assessment.controller;

import com.fahmak.alena.assessment.dto.QuizResponse;
import com.fahmak.alena.assessment.dto.SubmitAnswerResponse;
import com.fahmak.alena.assessment.dto.StartSessionRequest;
import com.fahmak.alena.assessment.dto.StartSessionResponse;
import com.fahmak.alena.assessment.dto.CheatEventBatchRequest;
import com.fahmak.alena.assessment.service.AssessmentService;
import com.fahmak.alena.assessment.service.IntegrityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final IntegrityService integrityService;

    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        return ResponseEntity.ok(assessmentService.getAllQuizzes());
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getQuizById(id));
    }

    @PostMapping("/questions/{questionId}/submit")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long questionId,
            @RequestBody Map<String, String> payload,
            org.springframework.security.core.Authentication authentication) {
        
        String selectedAnswer = payload.get("answer");
        if (selectedAnswer == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Answer is required"));
        }
        
        String email = authentication != null ? authentication.getName() : null;
        SubmitAnswerResponse response = assessmentService.submitAnswer(questionId, selectedAnswer, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sessions/start")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StartSessionResponse> startSession(@RequestBody StartSessionRequest request, org.springframework.security.core.Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(assessmentService.startSession(request, email));
    }

    @PostMapping("/sessions/{sessionId}/events")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> reportEvents(@PathVariable Long sessionId, @RequestBody CheatEventBatchRequest request, org.springframework.security.core.Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        integrityService.processEvents(sessionId, request.getEvents(), email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sessions/{sessionId}/submit")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitSession(@PathVariable Long sessionId, org.springframework.security.core.Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        integrityService.finalizeSession(sessionId, email);
        return ResponseEntity.ok().build();
    }
}
