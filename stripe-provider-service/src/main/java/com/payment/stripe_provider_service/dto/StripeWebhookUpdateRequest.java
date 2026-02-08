package com.payment.stripe_provider_service.dto;

public class StripeWebhookUpdateRequest {

    private String paymentId;
    private String stripeEventId;
    private String stripeEventType;
    private String stripeObjectId;
    private String stripeSessionId;
    private String stripePaymentIntentId;
    private String status; // SUCCESS / FAILED

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStripeEventId() {
        return stripeEventId;
    }

    public void setStripeEventId(String stripeEventId) {
        this.stripeEventId = stripeEventId;
    }

    public String getStripeEventType() {
        return stripeEventType;
    }

    public void setStripeEventType(String stripeEventType) {
        this.stripeEventType = stripeEventType;
    }

    public String getStripeObjectId() {
        return stripeObjectId;
    }

    public void setStripeObjectId(String stripeObjectId) {
        this.stripeObjectId = stripeObjectId;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

