package com.payment.payment_integration_service.dto;

import lombok.Data;

@Data
public class StripeWebhookUpdateRequest {
    private String paymentId;
    private String stripeEventId;
    private String stripeEventType;
    private String stripeObjectId;
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private String status; // SUCCESS / FAILED
}

