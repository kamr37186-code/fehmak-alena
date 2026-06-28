package com.fahmak.alena.assessment.service;

import com.fahmak.alena.assessment.dto.CheatEventDTO;
import com.fahmak.alena.assessment.entity.CheatEventType;
import com.fahmak.alena.assessment.entity.IntegrityVerdict;
import com.fahmak.alena.assessment.entity.QuizSession;
import com.fahmak.alena.assessment.repository.CheatEventRepository;
import com.fahmak.alena.assessment.repository.QuizSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IntegrityServiceTest {

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private CheatEventRepository cheatEventRepository;

    @InjectMocks
    private IntegrityService integrityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processEvents_ShouldUpdateSessionStats() {
        QuizSession session = new QuizSession();
        session.setId(1L);
        com.fahmak.alena.user.entity.User user = new com.fahmak.alena.user.entity.User();
        user.setEmail("test@test.com");
        session.setUser(user);
        when(quizSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        CheatEventDTO blurEvent = new CheatEventDTO();
        blurEvent.setEventType(CheatEventType.WINDOW_BLUR);

        CheatEventDTO focusEvent = new CheatEventDTO();
        focusEvent.setEventType(CheatEventType.WINDOW_FOCUS);
        focusEvent.setMetadata("{\"durationMs\": 5000}");

        integrityService.processEvents(1L, List.of(blurEvent, focusEvent), "test@test.com");

        assertEquals(1, session.getTotalFocusLossCount());
        assertEquals(5000L, session.getTotalFocusLossDurationMs());
        verify(cheatEventRepository, times(2)).save(any());
        verify(quizSessionRepository).save(session);
    }

    @Test
    void finalizeSession_SuspiciousVerdict() {
        QuizSession session = new QuizSession();
        session.setId(1L);
        com.fahmak.alena.user.entity.User user = new com.fahmak.alena.user.entity.User();
        user.setEmail("test@test.com");
        session.setUser(user);
        session.setTotalFocusLossCount(3); // 0.20
        session.setTotalFocusLossDurationMs(15000L); // 15 seconds -> 0.15
        // total risk = 0.35 -> SUSPICIOUS
        when(quizSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        integrityService.finalizeSession(1L, "test@test.com");

        assertEquals(0.35, session.getRiskScore(), 0.001);
        assertEquals(IntegrityVerdict.SUSPICIOUS, session.getIntegrityVerdict());
        verify(quizSessionRepository).save(session);
    }
    
    @Test
    void finalizeSession_FlaggedVerdict() {
        QuizSession session = new QuizSession();
        session.setId(2L);
        com.fahmak.alena.user.entity.User user = new com.fahmak.alena.user.entity.User();
        user.setEmail("test@test.com");
        session.setUser(user);
        session.setTotalFocusLossCount(5); // 0.35
        session.setTotalFocusLossDurationMs(35000L); // 35 seconds -> 0.25
        session.setCopyPasteCount(1); // 0.10
        // total risk = 0.70 -> FLAGGED
        when(quizSessionRepository.findById(2L)).thenReturn(Optional.of(session));

        integrityService.finalizeSession(2L, "test@test.com");

        assertEquals(0.70, session.getRiskScore(), 0.001);
        assertEquals(IntegrityVerdict.FLAGGED, session.getIntegrityVerdict());
        verify(quizSessionRepository).save(session);
    }
}
