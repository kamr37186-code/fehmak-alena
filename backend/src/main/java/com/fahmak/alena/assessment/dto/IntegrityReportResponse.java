package com.fahmak.alena.assessment.dto;

import com.fahmak.alena.assessment.entity.IntegrityVerdict;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IntegrityReportResponse {
    private Long sessionId;
    private String studentName;
    private String quizTitle;
    private Double riskScore;
    private IntegrityVerdict verdict;
    private Integer focusLossCount;
    private Long totalFocusLossDurationMs;
    private Integer rapidAnswerCount;
    private Integer copyPasteCount;
    private List<CheatEventDTO> events;
}
