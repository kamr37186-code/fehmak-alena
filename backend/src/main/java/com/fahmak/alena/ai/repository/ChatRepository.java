package com.fahmak.alena.ai.repository;

import com.fahmak.alena.ai.entity.ChatMessageEntity;
import com.fahmak.alena.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByUserOrderByTimestampAsc(User user);
}
