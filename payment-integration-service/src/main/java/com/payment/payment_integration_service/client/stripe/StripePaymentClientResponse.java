package com.payment.payment_integration_service.client.stripe;

import lombok.Data;

@Data
public class StripePaymentClientResponse {
    private String checkoutUrl;
    private String stripeSessionId;
}
