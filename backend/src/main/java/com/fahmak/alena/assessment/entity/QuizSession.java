package com.fahmak.alena.assessment.entity;

import com.fahmak.alena.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuizSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    @Column(name = "total_focus_loss_count")
    private Integer totalFocusLossCount = 0;

    @Column(name = "total_focus_loss_duration_ms")
    private Long totalFocusLossDurationMs = 0L;

    @Column(name = "rapid_answer_count")
    private Integer rapidAnswerCount = 0;

    @Column(name = "copy_paste_count")
    private Integer copyPasteCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "integrity_verdict")
    private IntegrityVerdict integrityVerdict = IntegrityVerdict.CLEAN;

    @Column(name = "risk_score")
    private Double riskScore = 0.0;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheatEvent> cheatEvents;
}
