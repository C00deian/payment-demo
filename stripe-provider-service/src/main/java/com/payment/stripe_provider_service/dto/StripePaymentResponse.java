package com.payment.stripe_provider_service.dto;


public class StripePaymentResponse {

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public StripePaymentResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    private String checkoutUrl;
    private String stripeSessionId;

}

