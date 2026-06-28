package com.fahmak.alena.payment.repository;

import com.fahmak.alena.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByStripePaymentIntentId(String stripePaymentIntentId);
}
