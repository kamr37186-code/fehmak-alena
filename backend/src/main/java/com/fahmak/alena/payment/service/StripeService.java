package com.fahmak.alena.payment.service;

import com.fahmak.alena.payment.dto.CheckoutRequest;
import com.fahmak.alena.payment.dto.CheckoutResponse;
import com.fahmak.alena.payment.entity.SubscriptionPlan;
import com.fahmak.alena.payment.repository.SubscriptionPlanRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final SubscriptionPlanRepository planRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public CheckoutResponse createCheckoutSession(CheckoutRequest request, String userEmail) throws Exception {
        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        SessionCreateParams params =
            SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(request.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(request.getCancelUrl())
                .setCustomerEmail(userEmail)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(plan.getStripePriceId())
                        .build()
                )
                .build();

        Session session = Session.create(params);

        return new CheckoutResponse(session.getId(), session.getUrl());
    }
}
