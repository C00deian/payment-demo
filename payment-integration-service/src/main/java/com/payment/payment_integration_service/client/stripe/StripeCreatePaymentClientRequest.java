package com.payment.payment_integration_service.client.stripe;

import lombok.Data;

@Data
public class StripeCreatePaymentClientRequest {

    private String paymentId;   // paymentId from integration service
    private Long amount;          // smallest unit
    private String currency;
    private String successUrl;
    private String failureUrl;

}
