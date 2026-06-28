package com.fahmak.alena.gamification.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GamificationProfileDTO {
    private Long userId;
    private int totalXp;
    private int level;
    private List<BadgeDTO> badges;
    private List<ActivityDTO> recentActivities;

    @Data
    @Builder
    public static class BadgeDTO {
        private Long id;
        private String name;
        private String description;
        private String iconUrl;
    }

    @Data
    @Builder
    public static class ActivityDTO {
        private Long id;
        private String activityType;
        private Integer xpGained;
        private String activityDate;
    }
}
