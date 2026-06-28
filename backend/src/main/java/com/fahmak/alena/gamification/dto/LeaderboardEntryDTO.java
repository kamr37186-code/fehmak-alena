package com.fahmak.alena.gamification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDTO {
    private int rank;
    private String firstName;
    private String lastName;
    private int totalXp;
}
