package com.fahmak.alena.immersive.repository;

import com.fahmak.alena.immersive.entity.ImmersiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImmersiveSessionRepository extends JpaRepository<ImmersiveSession, Long> {
    Optional<ImmersiveSession> findByUserEmail(String email);
}
