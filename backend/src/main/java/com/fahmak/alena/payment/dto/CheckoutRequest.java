package com.fahmak.alena.payment.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long planId;
    private String successUrl;
    private String cancelUrl;
}
