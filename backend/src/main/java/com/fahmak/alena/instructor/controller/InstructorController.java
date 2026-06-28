package com.fahmak.alena.instructor.controller;

import com.fahmak.alena.instructor.dto.InstructorDashboardResponse;
import com.fahmak.alena.assessment.dto.IntegrityReportResponse;
import com.fahmak.alena.instructor.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    private final InstructorService instructorService;

    @GetMapping("/dashboard")
    public ResponseEntity<InstructorDashboardResponse> getDashboardData(org.springframework.security.core.Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(instructorService.getDashboardData(email));
    }

    @GetMapping("/integrity/{quizId}")
    public ResponseEntity<List<IntegrityReportResponse>> getIntegrityReports(@PathVariable Long quizId, org.springframework.security.core.Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(instructorService.getIntegrityReports(quizId, email));
    }

    @GetMapping("/integrity/flags")
    public ResponseEntity<List<IntegrityReportResponse>> getFlaggedSessions(org.springframework.security.core.Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(instructorService.getFlaggedSessions(email));
    }
}
