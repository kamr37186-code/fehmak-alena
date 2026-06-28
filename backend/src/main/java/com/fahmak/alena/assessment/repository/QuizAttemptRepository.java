package com.fahmak.alena.assessment.repository;

import com.fahmak.alena.assessment.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
}
