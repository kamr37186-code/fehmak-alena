package com.fahmak.alena.gamification.entity;

import com.fahmak.alena.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gamification_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GamificationActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String activityType; // COURSE_COMPLETION, QUIZ_PASSED, STREAK
    private Integer xpGained;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_awarded_id")
    private Badge badgeAwarded;

    private LocalDateTime activityDate;
}
