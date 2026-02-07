package com.payment.payment_integration_service.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    // 🔹 Merchant side identifiers
    private String orderId;        // Merchant order reference

    // 🔹 Payment details
    private Long amount;           // Amount in smallest unit (paise / cents)
    private String currency;       // INR, USD, EUR

    // 🔹 PSP selection
    private String paymentProvider;       // STRIPE / PAYPAL

    // 🔹 Callback URLs (optional but real-world)
    private String successUrl;
    private String failureUrl;


    // getters & setters
}
