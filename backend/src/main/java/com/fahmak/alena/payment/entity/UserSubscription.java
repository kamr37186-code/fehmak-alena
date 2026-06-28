package com.fahmak.alena.payment.entity;

import com.fahmak.alena.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Column(unique = true)
    private String stripeSubscriptionId;

    private String stripeCustomerId;

    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;

    @Column(nullable = false)
    private String status; // ACTIVE, CANCELED, PAST_DUE
}
