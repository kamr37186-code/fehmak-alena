package com.fahmak.alena.assessment.repository;

import com.fahmak.alena.assessment.entity.QuizSession;
import com.fahmak.alena.assessment.entity.IntegrityVerdict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {
    Optional<QuizSession> findByUserIdAndQuizId(Long userId, Long quizId);
    List<QuizSession> findByQuizId(Long quizId);
    List<QuizSession> findByQuizIdAndIntegrityVerdictIn(Long quizId, List<IntegrityVerdict> verdicts);
}
