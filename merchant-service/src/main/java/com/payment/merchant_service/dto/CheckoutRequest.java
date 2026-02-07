package com.payment.merchant_service.dto;

import lombok.Data;

@Data
public class CheckoutRequest {

    private String orderId;
    private Long amount;
    private String currency;
    private String paymentProvider;
}
