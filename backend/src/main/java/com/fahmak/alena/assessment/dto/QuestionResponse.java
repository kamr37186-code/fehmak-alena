package com.fahmak.alena.assessment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String options; // Typically JSON string in DB, passing it to frontend as string or parsed
    private String type; // MULTIPLE_CHOICE, TRUE_FALSE
    
    // CRITICAL SECURITY: `correctAnswer` and `explanation` are deliberately omitted here.
}
