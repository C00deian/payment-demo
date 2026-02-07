package com.payment.payment_integration_service.dto;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentId;
    private String redirectUrl;
    private String status;
}

