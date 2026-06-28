package com.fahmak.alena.assessment.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private String difficultyLevel;
    private List<QuestionResponse> questions;
}
