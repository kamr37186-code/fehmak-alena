package com.fahmak.alena.gamification.repository;

import com.fahmak.alena.gamification.entity.GamificationActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamificationActivityRepository extends JpaRepository<GamificationActivity, Long> {
    List<GamificationActivity> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(g.xpGained), 0) FROM GamificationActivity g WHERE g.user.id = :userId")
    Integer getTotalXpByUserId(Long userId);

    @Query("SELECT g.user.firstName, g.user.lastName, COALESCE(SUM(g.xpGained), 0) as totalXp " +
           "FROM GamificationActivity g " +
           "GROUP BY g.user.id, g.user.firstName, g.user.lastName " +
           "ORDER BY totalXp DESC")
    List<Object[]> getLeaderboard(org.springframework.data.domain.Pageable pageable);
}
