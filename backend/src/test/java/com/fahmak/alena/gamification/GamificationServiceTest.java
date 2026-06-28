package com.fahmak.alena.gamification;

import com.fahmak.alena.gamification.entity.GamificationActivity;
import com.fahmak.alena.gamification.repository.GamificationActivityRepository;
import com.fahmak.alena.gamification.service.GamificationService;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import com.fahmak.alena.notification.service.NotificationService;
import com.fahmak.alena.notification.entity.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GamificationServiceTest {

    @Mock
    private GamificationActivityRepository activityRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private GamificationService gamificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void awardXp_shouldSaveActivityAndSendNotification() {
        User user = new User();
        user.setId(1L);

        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(activityRepository.save(any(GamificationActivity.class))).thenReturn(new GamificationActivity());

        gamificationService.awardXp(1L, "Completed Lesson", 60);

        verify(activityRepository, times(1)).save(any(GamificationActivity.class));
        verify(notificationService, times(1)).createNotification(
                eq(1L),
                eq("XP Awarded!"),
                eq("You earned 60 XP for: Completed Lesson"),
                eq(NotificationType.ACHIEVEMENT)
        );
    }
}
