package com.fahmak.alena.payment.integration;

import com.fahmak.alena.payment.entity.PaymentTransaction;
import com.fahmak.alena.payment.repository.PaymentTransactionRepository;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentIntegrationTest {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndRetrievePaymentTransaction() {
        // Create user
        User user = new User();
        user.setUsername("payer");
        user.setEmail("payer@test.com");
        user.setFirstName("Payer");
        user.setLastName("Test");
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        // Create transaction
        PaymentTransaction tx = new PaymentTransaction();
        tx.setUser(user);
        tx.setAmount(50.0);
        tx.setCurrency("USD");
        tx.setStatus("COMPLETED");
        tx.setStripePaymentIntentId("pi_12345");
        tx.setCreatedAt(LocalDateTime.now());
        
        tx = paymentTransactionRepository.save(tx);

        assertThat(tx.getId()).isNotNull();

        // Retrieve transaction
        Optional<PaymentTransaction> found = paymentTransactionRepository.findById(tx.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualTo(50.0);
        assertThat(found.get().getUser().getEmail()).isEqualTo("payer@test.com");
    }
}
