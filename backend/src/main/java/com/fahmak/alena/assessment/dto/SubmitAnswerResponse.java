package com.fahmak.alena.assessment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitAnswerResponse {
    private boolean isCorrect;
    private String aiFeedback;
    private String explanation;
}
