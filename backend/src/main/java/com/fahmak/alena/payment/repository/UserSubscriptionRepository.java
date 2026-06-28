package com.fahmak.alena.payment.repository;

import com.fahmak.alena.payment.entity.UserSubscription;
import com.fahmak.alena.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByUser(User user);
    Optional<UserSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
