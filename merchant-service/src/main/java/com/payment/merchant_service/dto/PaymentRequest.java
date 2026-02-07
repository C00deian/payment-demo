package com.payment.merchant_service.dto;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class PaymentRequest {

    private String orderId;
    private Long amount;
    private String currency;
    private String paymentProvider;      // STRIPE / PAYPAL
    private String successUrl;
    private String failureUrl;
}
