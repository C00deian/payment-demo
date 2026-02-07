package com.payment.payment_integration_service.dto;

import lombok.Data;

@Data
public class WebhookUpdateRequest {
    private String paymentId;
}
