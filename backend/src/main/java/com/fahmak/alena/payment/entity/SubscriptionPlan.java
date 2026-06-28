package com.fahmak.alena.payment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., BASIC, PREMIUM, ENTERPRISE

    @Column(nullable = false, unique = true)
    private String stripePriceId; // Stripe price identifier

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String currency = "usd";

    @Column(name = "billing_interval", nullable = false)
    private String interval = "month"; // month, year
}
