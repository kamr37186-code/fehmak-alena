package com.fahmak.alena.assessment.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheatEventBatchRequest {
    private List<CheatEventDTO> events;
}
