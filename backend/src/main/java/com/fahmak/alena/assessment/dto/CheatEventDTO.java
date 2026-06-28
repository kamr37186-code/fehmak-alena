package com.fahmak.alena.assessment.dto;

import com.fahmak.alena.assessment.entity.CheatEventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheatEventDTO {
    private CheatEventType eventType;
    private LocalDateTime timestamp;
    private String metadata;
}
