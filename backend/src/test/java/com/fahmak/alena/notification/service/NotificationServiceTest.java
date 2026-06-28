package com.fahmak.alena.notification.service;

import com.fahmak.alena.notification.dto.NotificationDTO;
import com.fahmak.alena.notification.entity.Notification;
import com.fahmak.alena.notification.entity.NotificationType;
import com.fahmak.alena.notification.repository.NotificationRepository;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNotification_ShouldSave() {
        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        notificationService.createNotification(1L, "Title", "Message", NotificationType.SYSTEM);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getUserNotifications_ShouldReturnList() {
        User user = new User();
        user.setId(1L);

        Notification n = Notification.builder()
                .id(10L)
                .title("Title")
                .message("Message")
                .type(NotificationType.SYSTEM)
                .user(user)
                .build();

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(n));

        List<NotificationDTO> result = notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }
}
