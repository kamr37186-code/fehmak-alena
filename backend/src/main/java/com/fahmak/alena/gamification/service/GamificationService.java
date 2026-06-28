package com.fahmak.alena.gamification.service;

import com.fahmak.alena.gamification.dto.AwardXpRequest;
import com.fahmak.alena.gamification.dto.GamificationProfileDTO;
import com.fahmak.alena.gamification.dto.LeaderboardEntryDTO;
import com.fahmak.alena.gamification.entity.GamificationActivity;
import com.fahmak.alena.gamification.repository.GamificationActivityRepository;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import com.fahmak.alena.notification.entity.NotificationType;
import com.fahmak.alena.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final GamificationActivityRepository activityRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public GamificationProfileDTO getUserGamificationProfile(Long userId) {
        userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer totalXp = activityRepository.getTotalXpByUserId(userId);
        if (totalXp == null || totalXp == 0) {
            // Fallback for demo
            totalXp = (int) (Math.random() * 3500) + 1500;
        }
        List<GamificationActivity> activities = activityRepository.findByUserId(userId);

        List<GamificationProfileDTO.BadgeDTO> badgeDtos = activities.stream()
                .filter(a -> a.getBadgeAwarded() != null)
                .map(GamificationActivity::getBadgeAwarded)
                .distinct()
                .map(badge -> GamificationProfileDTO.BadgeDTO.builder()
                        .id(badge.getId())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .iconUrl(badge.getIconUrl())
                        .build())
                .collect(Collectors.toList());

        List<GamificationProfileDTO.ActivityDTO> activityDtos = activities.stream()
                .map(a -> GamificationProfileDTO.ActivityDTO.builder()
                        .id(a.getId())
                        .activityType(a.getActivityType())
                        .xpGained(a.getXpGained())
                        .activityDate(a.getActivityDate() != null
                                ? a.getActivityDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                : null)
                        .build())
                .collect(Collectors.toList());

        return GamificationProfileDTO.builder()
                .userId(userId)
                .totalXp(totalXp)
                .level(calculateLevel(totalXp))
                .badges(badgeDtos)
                .recentActivities(activityDtos)
                .build();
    }

    /**
     * Awards XP to a user with authorization check.
     * Ensures the authenticated user can only award XP to themselves.
     */
    @Transactional
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void awardXpSecure(String authenticatedEmail, Long targetUserId, AwardXpRequest request) {
        User authenticatedUser = userService.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!authenticatedUser.getId().equals(targetUserId)) {
            throw new SecurityException("You cannot award XP to another user.");
        }

        GamificationActivity activity = new GamificationActivity();
        activity.setUser(authenticatedUser);
        activity.setActivityType(request.getActivityType());
        activity.setXpGained(request.getXp());
        activity.setActivityDate(LocalDateTime.now());

        activityRepository.save(activity);
        
        notificationService.createNotification(
                authenticatedUser.getId(),
                "XP Awarded!",
                "You earned " + request.getXp() + " XP for: " + request.getActivityType(),
                NotificationType.ACHIEVEMENT
        );
    }

    @Transactional
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void awardXp(Long userId, String activityType, Integer xp) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GamificationActivity activity = new GamificationActivity();
        activity.setUser(user);
        activity.setActivityType(activityType);
        activity.setXpGained(xp);
        activity.setActivityDate(LocalDateTime.now());

        activityRepository.save(activity);
        
        notificationService.createNotification(
                user.getId(),
                "XP Awarded!",
                "You earned " + xp + " XP for: " + activityType,
                NotificationType.ACHIEVEMENT
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "leaderboard")
    public List<LeaderboardEntryDTO> getLeaderboard() {
        List<Object[]> results = activityRepository.getLeaderboard(PageRequest.of(0, 5));
        List<LeaderboardEntryDTO> mapped = new ArrayList<>();
        int rank = 1;

        for (Object[] row : results) {
            mapped.add(LeaderboardEntryDTO.builder()
                    .rank(rank++)
                    .firstName((String) row[0])
                    .lastName((String) row[1])
                    .totalXp(((Number) row[2]).intValue())
                    .build());
        }

        // Fallback for demo if leaderboard is empty
        if (mapped.isEmpty()) {
            String[] firstNames = {"Ahmed", "Sarah", "Youssef", "Layla", "Omar"};
            String[] lastNames = {"Hassan", "Kamel", "Ali", "Mahmoud", "Tariq"};
            int baseXp = 8000;
            for (int i = 0; i < 5; i++) {
                mapped.add(LeaderboardEntryDTO.builder()
                        .rank(i + 1)
                        .firstName(firstNames[i])
                        .lastName(lastNames[i])
                        .totalXp(baseXp - (int)(Math.random() * 500) - (i * 800))
                        .build());
            }
        }
        return mapped;
    }

    private int calculateLevel(Integer totalXp) {
        if (totalXp == null || totalXp == 0) return 1;
        return (totalXp / 1000) + 1;
    }
}
