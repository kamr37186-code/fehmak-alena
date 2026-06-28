package com.fahmak.alena.payment.controller;

import com.fahmak.alena.payment.dto.CheckoutRequest;
import com.fahmak.alena.payment.dto.CheckoutResponse;
import com.fahmak.alena.payment.service.StripeService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutSession(@RequestBody CheckoutRequest request, org.springframework.security.core.Authentication auth) {
        try {
            String email = auth != null ? auth.getName() : null;
            return ResponseEntity.ok(stripeService.createCheckoutSession(request, email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            
            switch (event.getType()) {
                case "checkout.session.completed":
                    // Handle successful checkout
                    break;
                case "invoice.paid":
                    // Handle payment
                    break;
                case "customer.subscription.deleted":
                    // Handle cancellation
                    break;
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: " + e.getMessage());
        }
    }
}
