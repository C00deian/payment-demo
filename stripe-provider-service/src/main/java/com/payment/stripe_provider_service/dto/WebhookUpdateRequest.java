package com.payment.stripe_provider_service.dto;


public class WebhookUpdateRequest {
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    private String paymentId;
    public WebhookUpdateRequest(String paymentId){ this.paymentId = paymentId; }
}
