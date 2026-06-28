package com.fahmak.alena.gamification.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AwardXpRequest {

    @NotBlank(message = "Activity type is required")
    private String activityType;

    @NotNull(message = "XP amount is required")
    @Min(value = 1, message = "XP must be at least 1")
    private Integer xp;
}
