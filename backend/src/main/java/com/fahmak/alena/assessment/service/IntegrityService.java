package com.fahmak.alena.assessment.service;

import com.fahmak.alena.assessment.dto.CheatEventDTO;
import com.fahmak.alena.assessment.entity.CheatEvent;
import com.fahmak.alena.assessment.entity.CheatEventType;
import com.fahmak.alena.assessment.entity.IntegrityVerdict;
import com.fahmak.alena.assessment.entity.QuizSession;
import com.fahmak.alena.assessment.repository.CheatEventRepository;
import com.fahmak.alena.assessment.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegrityService {

    private final QuizSessionRepository quizSessionRepository;
    private final CheatEventRepository cheatEventRepository;

    @Transactional
    public void processEvents(Long sessionId, List<CheatEventDTO> events, String email) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getUser() == null || !session.getUser().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to modify this session");
        }

        for (CheatEventDTO dto : events) {
            CheatEvent event = new CheatEvent();
            event.setSession(session);
            event.setEventType(dto.getEventType());
            event.setEventTimestamp(dto.getTimestamp());
            event.setMetadata(dto.getMetadata());
            cheatEventRepository.save(event);

            // Update session counters
            if (dto.getEventType() == CheatEventType.WINDOW_BLUR) {
                session.setTotalFocusLossCount(session.getTotalFocusLossCount() + 1);
            } else if (dto.getEventType() == CheatEventType.WINDOW_FOCUS) {
                // If metadata contains durationMs, parse and add it
                if (dto.getMetadata() != null && dto.getMetadata().contains("durationMs")) {
                    try {
                        String json = dto.getMetadata();
                        String[] parts = json.split(":");
                        if (parts.length > 1) {
                            String msString = parts[1].replaceAll("[^0-9]", "");
                            long ms = Long.parseLong(msString);
                            session.setTotalFocusLossDurationMs(session.getTotalFocusLossDurationMs() + ms);
                        }
                    } catch (Exception e) {
                        // ignore parsing error
                    }
                }
            } else if (dto.getEventType() == CheatEventType.RAPID_ANSWER) {
                session.setRapidAnswerCount(session.getRapidAnswerCount() + 1);
            } else if (dto.getEventType() == CheatEventType.COPY_DETECTED || dto.getEventType() == CheatEventType.PASTE_DETECTED) {
                session.setCopyPasteCount(session.getCopyPasteCount() + 1);
            }
        }
        
        quizSessionRepository.save(session);
    }

    @Transactional
    public void finalizeSession(Long sessionId, String email) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getUser() == null || !session.getUser().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to finalize this session");
        }

        double riskScore = calculateRiskScore(session);
        session.setRiskScore(riskScore);
        session.setIntegrityVerdict(determineVerdict(riskScore));

        quizSessionRepository.save(session);
    }

    private double calculateRiskScore(QuizSession session) {
        double score = 0.0;

        if (session.getTotalFocusLossCount() >= 5) score += 0.35;
        else if (session.getTotalFocusLossCount() >= 3) score += 0.20;
        else if (session.getTotalFocusLossCount() >= 1) score += 0.05;

        long awaySeconds = session.getTotalFocusLossDurationMs() / 1000;
        if (awaySeconds >= 30) score += 0.25;
        else if (awaySeconds >= 10) score += 0.15;

        if (session.getRapidAnswerCount() >= 3) score += 0.20;
        else if (session.getRapidAnswerCount() >= 1) score += 0.10;

        if (session.getCopyPasteCount() >= 2) score += 0.20;
        else if (session.getCopyPasteCount() >= 1) score += 0.10;

        return Math.min(score, 1.0);
    }

    private IntegrityVerdict determineVerdict(double riskScore) {
        if (riskScore >= 0.60) return IntegrityVerdict.FLAGGED;
        if (riskScore >= 0.30) return IntegrityVerdict.SUSPICIOUS;
        return IntegrityVerdict.CLEAN;
    }
}
