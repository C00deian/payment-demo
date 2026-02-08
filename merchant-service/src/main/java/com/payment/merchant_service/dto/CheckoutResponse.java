package com.payment.merchant_service.dto;

import lombok.Data;

@Data
public class CheckoutResponse {
    private String paymentId;
    private String redirectUrl;
    private String status;
}
